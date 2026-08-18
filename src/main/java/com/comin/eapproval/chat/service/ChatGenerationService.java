package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.entity.ChatMessage;
import com.comin.eapproval.chat.entity.ChatMessageRole;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import com.comin.eapproval.approval.service.ApprovalToolService;
import com.comin.eapproval.document.service.DocumentToolService;
import com.comin.eapproval.regulation.service.RegulationToolService;

import java.util.List;
import java.util.Map;

import reactor.core.publisher.Flux;

/**
 * 최근 대화 맥락을 기반으로 일반 AI 응답을 생성하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class ChatGenerationService {

    private static final String SYSTEM_PROMPT = """
            1. 당신은 전자결재 시스템의 업무 보조 AI입니다.
            2. 사용자가 제공한 대화 맥락을 바탕으로 정확하고 간결하게 답변합니다.
            3. 문서 승인, 반려, 삭제, 권한 변경은 직접 실행하지 않습니다.
            4. 응답 길이는 한글 기준 공백 포함 500자 이내로 작성합니다.
            5. 응답은 일반 텍스트로만 작성하며 Markdown 문법인 별표(**), 해시(#), 백틱(`), 목록 기호(-)를 사용하지 않습니다.
            """;

    /** 현재 데이터 조회 Tool의 호출 원칙을 안내하는 공통 지침입니다. */
    private static final String AI_TOOL_INSTRUCTION = """
            1. 현재 DB 상태가 필요한 문서·결재·규정 질문은 이전 대화 답변을 사용하지 말고 관련 조회 Tool을 호출합니다.
            2. 문서 검색 Tool의 keyword와 status는 선택값입니다.입력되지 않은 조건은 전체 조건으로 검색합니다.
            3. Tool 응답에 포함된 정보만 사용자에게 안내합니다.
            4. 문서 ID, 제목, 상태, 날짜, 결재자, 본문을 임의로 생성하지 않습니다.
            5. 문서 요약 시 원문에 명시된 사실만 사용합니다.
            6. 원문에 없는 날짜, 기간, 일수, 담당자, 사유, 금액, 비고를 추론하지 않습니다.
            7. 확인되지 않은 정보는 “문서에 명시되어 있지 않습니다.”라고 답변합니다.
            8. 문서 목록 조회 후 요약을 요청받으면 Tool 응답에 포함된 정확한 documentId로 getDocument를 호출합니다.
            9. 정확한 documentId가 없으면 사용자에게 문서를 선택해 달라고 요청하고 ID를 추측하지 않습니다.
            10. 규정의 조항·항 조회 질문은 반드시 getRegulation Tool을 호출합니다.
            11. 이전 대화에서 같은 규정을 조회했더라도 규정 조항 질문마다 getRegulation을 다시 호출합니다.
            12. 질문에 제N조 또는 제N항이 포함되면 이전 답변을 사용하지 않습니다.
            13. getRegulation의 regulationId는 반드시 getRegulations 결과에서 가져옵니다.
            14. getRegulation 호출 결과가 실패하면 규정 내용을 추측하지 말고 조회 실패를 안내합니다.
            15. getRegulation을 호출하지 않은 경우 규정 조항의 내용을 답변하지 않습니다.
            """;

    private final ChatClient.Builder chatClientBuilder;

    private final RegulationToolService regulationToolService;
    private final DocumentToolService documentToolService;
    private final ApprovalToolService approvalToolService;

    /**
     * 서버가 인증한 사용자 정보를 ToolContext로 주입하여 AI 스트림을 생성합니다.
     *
     * @param messages AI에 전달할 대화 메시지
     * @param memberId 인증된 회원 ID
     * @param role 인증된 회원 역할
     * @return AI 응답 스트림
     */
    public Flux<ChatResponse> stream(List<ChatMessage> messages, Long memberId, String role) {
        // 현재 채팅방의 최근 대화 내용을 Spring AI가 이해하는 Message로 변환
        List<Message> promptMessages = messages.stream()
                .filter(message -> message.getContent() != null
                        && !message.getContent().isBlank())
                .map(this::toPromptMessage)
                .toList();

        // ChatClient가 내부적으로 Amazon Nova와 통신
        return chatClientBuilder
                .build()
                .prompt()
                .system(SYSTEM_PROMPT)
                .system(AI_TOOL_INSTRUCTION)
                .messages(promptMessages) // 기존 메시지 전달을 하면서 맥락을 이해함
                .tools( // Nova가 질문 내용을 분석해서 필요한 Tool만 선택
                        regulationToolService,
                        documentToolService,
                        approvalToolService
                )
                .toolContext(toolContext(memberId, role)) // 사용자 정보 전달
                .stream() // 스트리밍 모드로 호출
                .chatResponse();
    }

    /**
     * 인증 정보를 ToolContext 맵으로 변환합니다.
     *
     * @param memberId 인증된 회원 ID
     * @param role 인증된 회원 역할
     * @return Tool 실행 컨텍스트
     */
    private Map<String, Object> toolContext(Long memberId, String role) {
        if (memberId == null || role == null) {
            return Map.of();
        }
        return Map.of(ChatToolContext.MEMBER_ID, memberId, ChatToolContext.ROLE, role);
    }

    private Message toPromptMessage(ChatMessage message) {
        if (message.getRole() == ChatMessageRole.ASSISTANT) {
            return new AssistantMessage(message.getContent());
        }
        return new UserMessage(message.getContent());
    }
}

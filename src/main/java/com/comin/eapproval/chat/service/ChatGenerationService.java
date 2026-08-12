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
import com.comin.eapproval.approval.service.ApprovalDetailToolService;
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
            당신은 전자결재 시스템의 업무 보조 AI입니다.
            사용자가 제공한 대화 맥락을 바탕으로 정확하고 간결하게 답변합니다.
            문서 승인, 반려, 삭제, 권한 변경은 직접 실행하지 않습니다.
            확인할 수 없는 내용은 추측하지 않고 모른다고 답변합니다.
            """;

    /** 문서 검색 조건을 선택적으로 처리하도록 안내하는 Tool 지침입니다. */
    private static final String DOCUMENT_SEARCH_INSTRUCTION = """
            문서 검색 Tool의 keyword와 status는 모두 선택값입니다.
            사용자가 특정 키워드만 말하면 status 없이 검색하고, 특정 상태만 말하면 keyword 없이 검색합니다.
            사용자가 내 문서 목록을 요청하면 두 조건을 비워 전체 문서를 검색합니다.
            검색 결과가 있으면 추가 조건을 먼저 묻지 말고 결과를 바로 안내합니다.
            """;

    private final ChatClient.Builder chatClientBuilder;
    private final RegulationToolService regulationToolService;
    private final DocumentToolService documentToolService;
    private final ApprovalDetailToolService approvalDetailToolService;
    private final ApprovalToolService approvalToolService;

    /**
     * 최근 대화 목록을 Nova 2 Lite에 전달해 일반 응답을 생성한다.
     *
     * @param messages AI에 전달할 최근 대화 목록
     * @return 생성된 AI 응답
     */
    public String generate(List<ChatMessage> messages) {
        return generate(messages, null, null);
    }

    /**
     * 서버가 인증한 사용자 정보를 ToolContext로 주입하여 AI 응답을 생성합니다.
     *
     * @param messages AI에 전달할 대화 메시지
     * @param memberId 인증된 회원 ID
     * @param role 인증된 회원 역할
     * @return 생성된 AI 응답
     */
    public String generate(List<ChatMessage> messages, Long memberId, String role) {
        List<Message> promptMessages = messages.stream()
                .filter(message -> message.getContent() != null
                        && !message.getContent().isBlank())
                .map(this::toPromptMessage)
                .toList();

        return chatClientBuilder
                .build()
                .prompt()
                .system(SYSTEM_PROMPT)
                .system(DOCUMENT_SEARCH_INSTRUCTION)
                .messages(promptMessages)
                .tools(
                        regulationToolService,
                        documentToolService,
                        approvalDetailToolService,
                        approvalToolService
                )
                .toolContext(toolContext(memberId, role))
                .call()
                .content();
    }

    /**
     * 최근 대화 맥락을 Nova 스트리밍 응답으로 생성한다.
     *
     * @param messages AI에 전달할 최근 대화 목록
     * @return Nova 응답 스트림
     */
    public Flux<ChatResponse> stream(List<ChatMessage> messages) {
        return stream(messages, null, null);
    }

    /**
     * 서버가 인증한 사용자 정보를 ToolContext로 주입하여 AI 스트림을 생성합니다.
     *
     * @param messages AI에 전달할 대화 메시지
     * @param memberId 인증된 회원 ID
     * @param role 인증된 회원 역할
     * @return AI 응답 스트림
     */
    public Flux<ChatResponse> stream(List<ChatMessage> messages, Long memberId, String role) {
        List<Message> promptMessages = messages.stream()
                .filter(message -> message.getContent() != null
                        && !message.getContent().isBlank())
                .map(this::toPromptMessage)
                .toList();

        return chatClientBuilder
                .build()
                .prompt()
                .system(SYSTEM_PROMPT)
                .system(DOCUMENT_SEARCH_INSTRUCTION)
                .messages(promptMessages)
                .tools(
                        regulationToolService,
                        documentToolService,
                        approvalDetailToolService,
                        approvalToolService
                )
                .toolContext(toolContext(memberId, role))
                .stream()
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

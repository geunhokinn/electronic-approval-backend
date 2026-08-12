package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.response.RagSource;
import com.comin.eapproval.chat.dto.response.SseCompletedData;
import com.comin.eapproval.chat.dto.response.SseUsageData;
import com.comin.eapproval.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Nova 스트리밍 응답을 완료 답변으로 저장하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class ChatStreamingResponseService {

    private final ChatConversationService conversationService;

    /**
     * 스트림을 끝까지 수집하고 ASSISTANT 메시지와 완료 데이터를 생성한다.
     *
     * @param assistantMessage 처리 중인 ASSISTANT 메시지
     * @param responseStream Nova 응답 스트림
     * @param sources 규정 답변 출처
     * @return completed 이벤트 데이터
     */
    public SseCompletedData complete(
            ChatMessage assistantMessage,
            Flux<ChatResponse> responseStream,
            List<RagSource> sources
    ) {
        List<ChatResponse> responses = responseStream.collectList().block();
        if (responses == null || responses.isEmpty()) {
            throw new IllegalStateException("AI 응답이 비어 있습니다.");
        }

        String content = responses.stream()
                .map(this::extractContent)
                .filter(text -> text != null && !text.isEmpty())
                .reduce("", String::concat);
        ChatResponse lastResponse = responses.get(responses.size() - 1);
        SseUsageData usage = extractUsage(lastResponse);

        conversationService.completeAssistantMessage(
                assistantMessage.getId(),
                appendSources(content, sources),
                "amazon.nova-2-lite-v1:0",
                usage == null ? null : usage.getPromptTokens(),
                usage == null ? null : usage.getCompletionTokens(),
                usage == null ? null : usage.getTotalTokens()
        );

        return new SseCompletedData(
                assistantMessage.getId(),
                content,
                sources == null ? List.of() : sources,
                usage
        );
    }

    private String extractContent(ChatResponse response) {
        if (response == null || response.getResult() == null
                || response.getResult().getOutput() == null) {
            return "";
        }
        return response.getResult().getOutput().getText();
    }

    private SseUsageData extractUsage(ChatResponse response) {
        if (response == null || response.getMetadata() == null
                || response.getMetadata().getUsage() == null) {
            return null;
        }
        var usage = response.getMetadata().getUsage();
        return new SseUsageData(
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens()
        );
    }

    private String appendSources(String content, List<RagSource> sources) {
        if (sources == null || sources.isEmpty()) {
            return content;
        }

        StringBuilder result = new StringBuilder(content).append("\n\n[출처]\n");
        sources.forEach(source -> result
                .append("- ").append(source.getRegulationName())
                .append(" ").append(source.getArticleNumber())
                .append(" ").append(source.getArticleTitle())
                .append("\n  버전: ").append(source.getVersion())
                .append("\n  시행일: ").append(source.getEffectiveDate())
                .append("\n"));
        return result.toString().trim();
    }
}

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

import java.util.List;

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

    private final ChatClient.Builder chatClientBuilder;

    /**
     * 최근 대화 목록을 Nova 2 Lite에 전달해 일반 응답을 생성한다.
     *
     * @param messages AI에 전달할 최근 대화 목록
     * @return 생성된 AI 응답
     */
    public String generate(List<ChatMessage> messages) {
        List<Message> promptMessages = messages.stream()
                .map(this::toPromptMessage)
                .toList();

        return chatClientBuilder
                .build()
                .prompt()
                .system(SYSTEM_PROMPT)
                .messages(promptMessages)
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
        List<Message> promptMessages = messages.stream()
                .map(this::toPromptMessage)
                .toList();

        return chatClientBuilder
                .build()
                .prompt()
                .system(SYSTEM_PROMPT)
                .messages(promptMessages)
                .stream()
                .chatResponse();
    }

    private Message toPromptMessage(ChatMessage message) {
        if (message.getRole() == ChatMessageRole.ASSISTANT) {
            return new AssistantMessage(message.getContent());
        }
        return new UserMessage(message.getContent());
    }
}

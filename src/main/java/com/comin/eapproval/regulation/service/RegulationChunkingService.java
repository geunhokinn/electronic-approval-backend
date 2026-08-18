package com.comin.eapproval.regulation.service;

import com.comin.eapproval.regulation.dto.RegulationTextChunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown 규정 문서를 조항과 항 헤딩 기준으로 검색용 청크로 분할합니다.
 */
@Service
public class RegulationChunkingService {

    private static final Pattern ARTICLE_PATTERN = Pattern.compile("^##\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("^###\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern TITLE_PATTERN = Pattern.compile("^#\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern ARTICLE_NUMBER_PATTERN = Pattern.compile("^(제\\s*\\d+조)");
    private static final Pattern PARAGRAPH_NUMBER_PATTERN = Pattern.compile("^(제\\s*\\d+항)");

    /**
     * Markdown 원문을 조항과 항 헤딩 기준으로 분할합니다.
     *
     * @param markdown Markdown 규정 원문
     * @param category 규정 분류 코드
     * @return 규정 검색용 청크 목록
     */
    public List<RegulationTextChunk> chunk(String markdown, String category) {
        String normalizedMarkdown = normalize(markdown);
        String regulationName = findTitle(normalizedMarkdown);
        List<RegulationTextChunk> chunks = new ArrayList<>();
        int chunkIndex = 0;

        for (ArticleSection article : findArticles(normalizedMarkdown)) {
            for (ParagraphSection paragraph : splitArticle(article.body())) {
                String trimmedContent = paragraph.body().trim();
                if (trimmedContent.isEmpty()) {
                    continue;
                }
                String prefix = buildPrefix(regulationName, category, article, paragraph);
                chunks.add(new RegulationTextChunk(
                        regulationName,
                        article.number(),
                        article.title(),
                        paragraph.number(),
                        chunkIndex++,
                        trimmedContent,
                        prefix + "\n\n" + trimmedContent
                ));
            }
        }
        return chunks;
    }

    private List<ArticleSection> findArticles(String markdown) {
        List<ArticleSection> articles = new ArrayList<>();
        Matcher matcher = ARTICLE_PATTERN.matcher(markdown);
        List<Integer> starts = new ArrayList<>();
        List<Integer> bodyEnds = new ArrayList<>();
        List<String> headings = new ArrayList<>();
        while (matcher.find()) {
            starts.add(matcher.end());
            bodyEnds.add(matcher.start());
            headings.add(matcher.group(1).trim());
        }
        for (int index = 0; index < starts.size(); index++) {
            int bodyEnd = index + 1 < starts.size() ? bodyEnds.get(index + 1) : markdown.length();
            String heading = headings.get(index);
            articles.add(new ArticleSection(
                    extractArticleNumber(heading),
                    extractArticleTitle(heading),
                    markdown.substring(starts.get(index), bodyEnd).trim()
            ));
        }
        return articles;
    }

    private List<ParagraphSection> splitArticle(String articleBody) {
        List<ParagraphSection> paragraphs = new ArrayList<>();
        Matcher matcher = PARAGRAPH_PATTERN.matcher(articleBody);
        List<Integer> starts = new ArrayList<>();
        List<Integer> bodyEnds = new ArrayList<>();
        List<String> headings = new ArrayList<>();
        while (matcher.find()) {
            starts.add(matcher.end());
            bodyEnds.add(matcher.start());
            headings.add(matcher.group(1).trim());
        }
        if (starts.isEmpty()) {
            return List.of(new ParagraphSection("없음", articleBody));
        }
        if (bodyEnds.get(0) > 0) {
            String before = articleBody.substring(0, bodyEnds.get(0)).trim();
            if (!before.isEmpty()) {
                paragraphs.add(new ParagraphSection("없음", before));
            }
        }
        for (int index = 0; index < starts.size(); index++) {
            int bodyEnd = index + 1 < starts.size() ? bodyEnds.get(index + 1) : articleBody.length();
            paragraphs.add(new ParagraphSection(
                    extractParagraphNumber(headings.get(index)),
                    articleBody.substring(starts.get(index), bodyEnd).trim()
            ));
        }
        return paragraphs;
    }

    private String buildPrefix(String name, String category, ArticleSection article, ParagraphSection paragraph) {
        return "[규정명: %s]\n[분류: %s]\n[조항: %s %s]\n[항: %s]"
                .formatted(name, category, article.number(), article.title(), paragraph.number());
    }

    private String findTitle(String markdown) {
        Matcher matcher = TITLE_PATTERN.matcher(markdown);
        return matcher.find() ? matcher.group(1).trim() : "미지정 규정";
    }

    private String extractArticleNumber(String heading) {
        Matcher matcher = ARTICLE_NUMBER_PATTERN.matcher(heading);
        return matcher.find() ? matcher.group(1) : heading;
    }

    private String extractArticleTitle(String heading) {
        return heading.replaceFirst("^제\\s*\\d+조\\s*", "").trim();
    }

    private String extractParagraphNumber(String heading) {
        Matcher matcher = PARAGRAPH_NUMBER_PATTERN.matcher(heading);
        return matcher.find() ? matcher.group(1) : heading;
    }

    private String normalize(String text) {
        return text == null ? "" : text.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace("\uFEFF", "")
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private record ArticleSection(String number, String title, String body) {
    }

    private record ParagraphSection(String number, String body) {
    }
}

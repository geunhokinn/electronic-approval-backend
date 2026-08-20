package com.comin.eapproval.member.init;

import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.repository.MemberRepository;
import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애플리케이션 시작 시 관리자 계정을 초기화한다.
 *
 * 환경변수에 설정된 관리자 계정이 존재하지 않는 경우에만 생성한다.
 */
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    @Value("${admin.name}")
    private String name;

    @Value("${admin.headquarters}")
    private String headquarters;

    @Value("${admin.team}")
    private String team;

    @Value("${admin.position}")
    private String position;

    private final MemberRepository memberRepository;
    private final DocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 애플리케이션 시작 시 관리자 계정을 생성한다.
     *
     * @param args 실행 인자
     */
    @Override
    @Transactional
    public void run(String... args) {

        Member admin = memberRepository.findByUsername(username)
                .orElseGet(() -> memberRepository.save(Member.createAdmin(
                        username,
                        passwordEncoder.encode(password),
                        name,
                        headquarters,
                        team,
                        position
                )));

        createSampleDocuments(admin);
    }

    /**
     * 관리자 계정으로 시연용 문서 17건을 생성한다.
     *
     * @param admin 문서 작성자로 사용할 관리자
     */
    private void createSampleDocuments(Member admin) {
        for (int index = 1; index <= 17; index++) {
            String title = "테스트 제목" + index;
            if (documentRepository.existsByWriterIdAndTitleAndDeletedAtIsNull(admin.getId(), title)) {
                continue;
            }

            documentRepository.save(Document.createDraft(
                    title,
                    "테스트 내용" + index,
                    admin,
                    null
            ));
        }
    }
}

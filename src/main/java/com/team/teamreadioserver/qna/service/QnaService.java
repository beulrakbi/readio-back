package com.team.teamreadioserver.qna.service;

import com.team.teamreadioserver.qna.dto.QnaAnswerDTO;
import com.team.teamreadioserver.qna.dto.QnaDetailDTO;
import com.team.teamreadioserver.qna.dto.QnaQuestionDTO;
import com.team.teamreadioserver.qna.dto.QnaResponseDTO;
import com.team.teamreadioserver.qna.entity.Qna;
import com.team.teamreadioserver.qna.repository.QnaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication; // Authentication import 추가

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QnaService {
    @Autowired
    private QnaRepository qnaRepository;
    //질문 등록
    public void wirteQna(QnaQuestionDTO qnaQuestionDTO) {
        Qna qna = Qna.builder()
                .qnaTitle(qnaQuestionDTO.getQnaTitle())
                .qnaQuestion(qnaQuestionDTO.getQnaQuestion())
                .build();
        qnaRepository.save(qna);
    }

    //질문 수정
    @Transactional
    public void updateQna(QnaQuestionDTO qnaQuestionDTO) {
        // 현재 로그인한 사용자 ID 가져오기 (관리자만 수정 가능하게 하려면 이 로직을 변경)
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        Qna qna = qnaRepository.findById(qnaQuestionDTO.getQnaId())
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));

        // 작성자 검증 (필요에 따라 관리자만 수정 가능하도록 로직 변경)
        if (!qna.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("자신이 작성한 게시글만 수정할 수 있습니다.");
        }

        qna.updateQuestion(
                qnaQuestionDTO.getQnaTitle(),
                qnaQuestionDTO.getQnaQuestion()
        );
    }
    //질문 삭제
    @Transactional
    public void deleteQna(Integer qnaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();

        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(()-> new IllegalArgumentException("해당 질문이 없습니다."));

        // 사용자의 권한 확인
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")); // "ROLE_ADMIN"은 실제 관리자 역할명에 따라 변경될 수 있습니다.

        // 관리자이거나, 작성자 본인인 경우에만 삭제 허용
        if (isAdmin || qna.getUserId().equals(currentUserId)) {
            qnaRepository.delete(qna);
        } else {
            throw new IllegalArgumentException("자신이 작성한 게시글만 삭제할 수 있습니다.");
        }
    }

    // 답변 등록 / 수정
    @Transactional
    public void updateQnaAnswer(QnaAnswerDTO qnaAnswerDTO) {
        Qna qna = qnaRepository.findById(qnaAnswerDTO.getQnaId())
                .orElseThrow(()-> new IllegalArgumentException("해당 질문이 없습니다."));
        qna.createAnswer( // 기존에 createAnswer로 되어 있던 메서드 사용
                qnaAnswerDTO.getQnaAnswer()
        );
        // save를 명시적으로 호출할 필요 없음: @Transactional 어노테이션이 붙은 메서드 내에서 엔티티의 상태가 변경되면
        // 트랜잭션이 커밋될 때 자동으로 변경사항이 데이터베이스에 반영됩니다 (Dirty Checking).
    }

    // 답변 삭제
    @Transactional
    public void deleteQnaAnswer(QnaAnswerDTO qnaAnswerDTO) { // ✨ 새로운 메서드 추가
        Qna qna = qnaRepository.findById(qnaAnswerDTO.getQnaId())
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));
        qna.createAnswer(null); // 답변을 null로 설정하여 삭제 효과
        // save를 명시적으로 호출할 필요 없음
    }


    public List<QnaResponseDTO> getQnaList(){
        List<Qna> qnas = qnaRepository.findAllByOrderByQnaCreateAtDesc();

        return qnas.stream()
                .map(qnas1 -> new QnaResponseDTO(
                        qnas1.getQnaId(),
                        qnas1.getQnaTitle(),
                        qnas1.getQnaCreateAt(),
                        qnas1.getQnaView()
                ))
                .collect(Collectors.toList());
    }

    // 질문 상세 조회 (조회수 증가 로직 추가)
    @Transactional // ✨ 조회수 증가를 위해 @Transactional 추가
    public QnaDetailDTO getQnaDetail(Integer qnaId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QNA가 없습니다."));

        // ✨ 조회수 1 증가
        qna.setQnaView(qna.getQnaView() + 1);
        // 트랜잭션 내에서 엔티티가 변경되었으므로 별도의 save 호출 없이 자동 저장됩니다.

        return new QnaDetailDTO(
                qna.getQnaId(),
                qna.getQnaTitle(),
                qna.getQnaQuestion(),
                qna.getQnaAnswer(),
                qna.getQnaCreateAt(),
                qna.getUserId()
        );
    }

    public List<QnaResponseDTO> searchQnaByTitle(String keyword) { // ✨ 파라미터 이름을 keyword로 수정
        List<Qna> qna = qnaRepository.findByQnaTitleContainingIgnoreCase(keyword); // ✨ 파라미터 이름을 keyword로 수정

        return qna.stream()
                .map(QnaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<QnaResponseDTO> getMyQnaList() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Qna> myQnaList = qnaRepository.findByUserId(userId);
        return myQnaList.stream()
                .map(QnaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
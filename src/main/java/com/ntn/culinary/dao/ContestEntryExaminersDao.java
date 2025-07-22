package com.ntn.culinary.dao;

import com.ntn.culinary.model.ContestEntryExaminers;

import java.util.List;

public interface ContestEntryExaminersDao {
    void addContestEntryExaminer(ContestEntryExaminers contestEntryExaminers);

    void updateContestEntryExaminer(ContestEntryExaminers contestEntryExaminers);

    boolean existsById(int id);

    // Lấy tất cả các bài thi mà giám khảo đã chấm
    List<ContestEntryExaminers> getAllContestEntryExaminersByExaminerId(int examinerId);

    // Lấy chi tiết một bài thi của giám khảo đã chấm
    ContestEntryExaminers getContestEntryExaminerById(int id);

    // Lấy tất cả các giám khảo đã chấm bài thi của một bài dự thi
    List<ContestEntryExaminers> getContestEntryExaminersByContestEntryId(int contestEntryId);

    // Xóa một bài thi theo id của bài thi và id của giám khảo
    void deleteByContestEntryIdAndExaminerId(int contestEntryId, int examinerId);

    // Kiểm tra xem giám khảo đã chấm bài thi theo id của bài thi và id của giám khảo hay chưa
    boolean existsByContestEntryIdAndExaminerId(int contestEntryId, int examinerId);


}

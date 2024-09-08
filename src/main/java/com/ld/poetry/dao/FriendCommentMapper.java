package com.ld.poetry.dao;
import com.ld.poetry.entity.Comment;
import com.ld.poetry.utils.CommonConst;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.poetry.entity.FriendComment;

import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.dao
 * @ClassName FriendCommentMapper
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
public interface FriendCommentMapper extends BaseMapper<FriendComment> {



    void save(FriendComment comment);

    List<FriendComment> findAll();


    Integer getFriendCommentCount();

    List<FriendComment> findAllByParentCommentIdOrderByCreateTime(@Param("parentCommentId")Integer parentCommentId);



    List<FriendComment> findAllByFloorCommentIdOrderByCreateTime(@Param("floorCommentId")Integer floorCommentId);







}

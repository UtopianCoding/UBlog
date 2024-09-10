package com.ld.poetry.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ld.poetry.config.UResult;
import com.ld.poetry.dao.CommentMapper;
import com.ld.poetry.dao.FriendCommentMapper;
import com.ld.poetry.entity.Comment;
import com.ld.poetry.entity.FriendComment;
import com.ld.poetry.service.CommentService;
import com.ld.poetry.service.FriendCommentService;
import com.ld.poetry.utils.CommonConst;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.CommentVO;
import com.ld.poetry.vo.friend.FriendCommentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service.impl
 * @ClassName FriendCommentServiceImpl
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class FriendCommentServiceImpl extends ServiceImpl<FriendCommentMapper, FriendComment> implements FriendCommentService {


    @Autowired
    private FriendCommentMapper friendCommentMapper;

    @Override
    public void saveFriendComment(FriendComment comment) {
        friendCommentMapper.save(comment);
    }

    @Override
    public Integer getFriendCommentCount() {
        return friendCommentMapper.getFriendCommentCount();
    }

    @Override
    public UResult<BaseRequestVO> listComment(BaseRequestVO baseRequestVO) {


        if (baseRequestVO.getFloorCommentId() == null) {
            lambdaQuery().eq(FriendComment::getParentCommentId, CommonConst.FIRST_COMMENT).orderByAsc(FriendComment::getCreateTime).page(baseRequestVO);
            List<FriendComment> comments = baseRequestVO.getRecords();
            if (CollectionUtils.isEmpty(comments)) {
                return UResult.success(baseRequestVO);
            }
            List<FriendCommentVo> commentVOs = comments.stream().map(c -> {
                FriendCommentVo commentVO = buildCommentVO(c);
                Page page = new Page(1, 5);
                lambdaQuery().eq(FriendComment::getFloorCommentId, c.getId()).orderByAsc(FriendComment::getCreateTime).page(page);
                List<FriendComment> childComments = page.getRecords();
                if (childComments != null) {
                    List<FriendCommentVo> ccVO = childComments.stream().map(cc -> buildCommentVO(cc)).collect(Collectors.toList());
                    page.setRecords(ccVO);
                }
                commentVO.setChildComments(page);
                return commentVO;
            }).collect(Collectors.toList());
            baseRequestVO.setRecords(commentVOs);
        } else {
            lambdaQuery().eq(FriendComment::getFloorCommentId, baseRequestVO.getFloorCommentId()).orderByAsc(FriendComment::getCreateTime).page(baseRequestVO);
            List<FriendComment> childComments = baseRequestVO.getRecords();
            if (CollectionUtils.isEmpty(childComments)) {
                return UResult.success(baseRequestVO);
            }
            List<FriendCommentVo> ccVO = childComments.stream().map(cc -> buildCommentVO(cc)).collect(Collectors.toList());
            baseRequestVO.setRecords(ccVO);
        }
        return UResult.success(baseRequestVO);

    }

    private FriendCommentVo buildCommentVO(FriendComment c) {
        FriendCommentVo commentVO = new FriendCommentVo();
        BeanUtils.copyProperties(c, commentVO);
        commentVO.setUsername(c.getName());





        if (commentVO.getParentUserId() != null) {

//            commentVO.setParentUsername();
//
//            if (u != null) {
//                commentVO.setParentUsername(u.getUsername());
//            }
//            if (!StringUtils.hasText(commentVO.getParentUsername())) {
//                commentVO.setParentUsername(UBUtil.getRandomName(commentVO.getParentUserId().toString()));
//            }
        }
        return commentVO;
    }

}

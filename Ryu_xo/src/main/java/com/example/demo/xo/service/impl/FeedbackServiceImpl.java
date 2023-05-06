package com.example.demo.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.commons.entity.Feedback;
import com.example.demo.commons.entity.User;
import com.example.demo.utils.ResultUtil;
import com.example.demo.utils.StringUtils;
import com.example.demo.xo.global.MessageConf;
import com.example.demo.xo.global.SQLConf;
import com.example.demo.xo.global.SysConf;
import com.example.demo.xo.mapper.FeedbackMapper;
import com.example.demo.xo.service.FeedbackService;
import com.example.demo.xo.service.UserService;
import com.example.demo.xo.vo.FeedbackVO;
import com.example.demo.base.enums.EStatus;
import com.example.demo.base.holder.RequestHolder;
import com.example.demo.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 反馈表 服务实现类
 *
 * @author 
 * @date 2018-09-08
 */
@Service
public class FeedbackServiceImpl extends SuperServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private UserService userService;

    @Override
    public IPage<Feedback> getPageList(FeedbackVO feedbackVO) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(feedbackVO.getTitle())) {
            queryWrapper.like(SQLConf.TITLE, feedbackVO.getTitle());
        }

        if (feedbackVO.getFeedbackStatus() != null) {
            queryWrapper.eq(SQLConf.FEEDBACK_STATUS, feedbackVO.getFeedbackStatus());
        }

        Page<Feedback> page = new Page<>();
        page.setCurrent(feedbackVO.getCurrentPage());
        page.setSize(feedbackVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<Feedback> pageList = feedbackService.page(page, queryWrapper);

        List<Feedback> feedbackList = pageList.getRecords();
        List<String> userUids = new ArrayList<>();
        feedbackList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                userUids.add(item.getUserUid());
            }
        });
        List<User> userList = userService.getUserListByIds(userUids);
        Map<String, User> map = new HashMap<>();
        userList.forEach(item -> {
            item.setPassWord("");
            map.put(item.getUid(), item);
        });

        feedbackList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(map.get(item.getUserUid()));
            }
        });

        pageList.setRecords(feedbackList);
        return pageList;
    }

    @Override
    public String addFeedback(FeedbackVO feedbackVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        Feedback feedback = feedbackService.getById(feedbackVO.getUid());
        feedback.setTitle(feedbackVO.getTitle());
        feedback.setContent(feedbackVO.getContent());
        feedback.setFeedbackStatus(feedbackVO.getFeedbackStatus());
        feedback.setReply(feedbackVO.getReply());
        feedback.setUpdateTime(new Date());
        if (request.getAttribute(SysConf.ADMIN_UID) != null) {
            feedback.setAdminUid(request.getAttribute(SysConf.ADMIN_UID).toString());
        }
        feedback.setUpdateTime(new Date());
        feedback.updateById();
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String editFeedback(FeedbackVO feedbackVO) {
        return null;
    }

    @Override
    public String deleteBatchFeedback(List<FeedbackVO> feedbackVOList) {
        HttpServletRequest request = RequestHolder.getRequest();
        final String adminUid = request.getAttribute(SysConf.ADMIN_UID).toString();
        if (feedbackVOList.size() <= 0) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        feedbackVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<Feedback> feedbackList = feedbackService.listByIds(uids);

        feedbackList.forEach(item -> {
            item.setAdminUid(adminUid);
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });

        Boolean save = feedbackService.updateBatchById(feedbackList);

        if (save) {
            return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.result(SysConf.ERROR, MessageConf.DELETE_FAIL);
        }
    }
}

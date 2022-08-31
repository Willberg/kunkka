package fun.johntaylor.kunkka.service.oj.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fun.johntaylor.kunkka.entity.oj.Oj;
import fun.johntaylor.kunkka.repository.mybatis.oj.OjMapper;
import fun.johntaylor.kunkka.service.oj.OjService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author john
 * @Description
 * @Date 2022/8/26 下午10:18
 */
@Service
@Slf4j
public class OjServiceImpl implements OjService {
    @Autowired
    private OjMapper ojMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Oj> add(Oj oj) {
        int sum = ojMapper.countBegin(oj.getUid(), Oj.S_BEGIN);
        if (sum > 0) {
            return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "请先暂停或完成已经开始的题目");
        }
        List<Oj> lists = ojMapper.searchListByUidPidOjTye(oj.getUid(), oj.getPid(), oj.getOjType());
        for (Oj old : lists) {
            if (!Oj.S_DEL.equals(old.getStatus())) {
                return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "该题目已经存在");
            }
        }
        ojMapper.insert(oj);
        return Result.success(oj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Oj> update(Oj oj) {
        Oj old = ojMapper.select(oj.getId());
        if (Oj.S_BEGIN.equals(old.getStatus()) && !Oj.S_DEL.equals(oj.getStatus())) {
            oj.setUseTime(old.getUseTime() + (oj.getPreTime() - old.getPreTime()) / 1000);
        }
        ojMapper.update(oj);
        return Result.success(oj);
    }

    @Override
    public Result<List<Oj>> searchListByUidTime(Long uid, Integer offset, Integer count, Long begin, Long end) {
        return Result.success(ojMapper.searchListByUidTime(uid, offset, count, begin, end));
    }

    @Override
    public Result<Integer> countByUidTime(Long uid, Long begin, Long end) {
        return Result.success(ojMapper.countByUidTime(uid, begin, end));
    }
}

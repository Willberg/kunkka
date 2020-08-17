package fun.johntaylor.kunkka.service.timer.impl;

import fun.johntaylor.kunkka.entity.timer.Timer;
import fun.johntaylor.kunkka.repository.mybatis.timer.TimerMapper;
import fun.johntaylor.kunkka.service.timer.TimerService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import fun.johntaylor.kunkka.utils.time.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @Author John
 * @Description 计时器服务
 * @Date 2020/8/12 8:00 PM
 **/
@Service
@Slf4j
public class TimerServiceImpl implements TimerService {
	@Autowired
	private TimerMapper timerMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<Timer> add(Timer timer) {
		Timer lastOne = timerMapper.searchLastOne(timer.getUid());
		if (Objects.isNull(lastOne)) {
			timer.setStatus(Timer.S_OPEN);
		} else {
			if (Timer.S_CLOSED.equals(lastOne.getStatus())) {
				timer.setStatus(Timer.S_OPEN);
			} else {
				timer.setStatus(Timer.S_CLOSED);
			}
		}

		if (Timer.S_CLOSED.equals(timer.getStatus())) {
			if (Objects.isNull(timer.getRelatedId())) {
				return Result.failWithCustomMessage("关联ID必须设置");
			}

			Timer relatedOne = timerMapper.searchRelatedOne(timer.getRelatedId());
			if (Objects.isNull(relatedOne) || !relatedOne.getUid().equals(timer.getUid())) {
				return Result.failWithCustomMessage("没有关联的计时器");
			}

			if (!timer.getType().equals(relatedOne.getType()) || !Timer.S_OPEN.equals(relatedOne.getStatus())) {
				return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "计时器类别或状态错误");
			}

			if (Objects.nonNull(relatedOne.getRelatedId())) {
				return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "该计时器已关闭");
			}
		} else {
			if (Objects.nonNull(timer.getRelatedId())) {
				return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "刚打开的计时器没有关联计时器");
			}

			if (Objects.nonNull(lastOne) && Timer.S_OPEN.equals(lastOne.getStatus())) {
				return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "上一个计时器未关闭");
			}
		}

		timerMapper.insert(timer);
		if (Timer.S_CLOSED.equals(timer.getStatus())) {
			Timer t = new Timer();
			t.setId(timer.getRelatedId());
			t.setRelatedId(timer.getId());
			t.setUpdateTime(System.currentTimeMillis());
			timerMapper.update(t);
		}
		return Result.success(timer);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<Timer> update(Timer timer) {
		Timer old = timerMapper.select(timer.getId());
		if (Objects.isNull(old) || !old.getUid().equals(timer.getUid())) {
			return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "不存在该计时器");
		}

		if (Objects.nonNull(timer.getCreateTime())) {
			// 创建时间不能改在别的计时器中间
			if (timer.getCreateTime() < old.getCreateTime()) {
				Timer recentOld = timerMapper.searchRecentOld(timer.getId(), timer.getUid());
				if (Objects.nonNull(recentOld) && recentOld.getCreateTime() > timer.getCreateTime()) {
					String dateTime = TimeUtil.getLocalDateTimeStrFromTimestamp(recentOld.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
					return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "创建时间不能小于" + dateTime);
				}
			} else if (timer.getCreateTime() > old.getCreateTime()) {
				Timer recentNew = timerMapper.searchRecentNew(timer.getId(), timer.getUid());
				if (Objects.nonNull(recentNew) && recentNew.getCreateTime() < timer.getCreateTime()) {
					String dateTime = TimeUtil.getLocalDateTimeStrFromTimestamp(recentNew.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
					return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "创建时间不能大于" + dateTime);
				}
			}
		}

		Timer relatedOne = null;
		if (Timer.S_CLOSED.equals(old.getStatus())) {
			relatedOne = timerMapper.searchRelatedOne(old.getRelatedId());
			if (Objects.nonNull(timer.getCreateTime()) && timer.getCreateTime() < relatedOne.getCreateTime()) {
				return Result.failWithCustomMessage("不能比关联的计时器的创建时间小");
			}
		} else {
			if (Objects.nonNull(old.getRelatedId())) {
				relatedOne = timerMapper.searchRelatedOne(old.getRelatedId());
				if (Objects.nonNull(timer.getCreateTime()) && timer.getCreateTime() > relatedOne.getCreateTime()) {
					return Result.failWithCustomMessage("不能比关联的计时器的创建时间大");
				}
			}
		}

		timerMapper.update(timer);
		if (Objects.nonNull(timer.getType()) && !timer.getType().equals(old.getType())) {
			// 更新关联计时器
			if (Objects.nonNull(relatedOne)) {
				Timer t = new Timer();
				t.setId(relatedOne.getId());
				t.setType(timer.getType());
				t.setUpdateTime(System.currentTimeMillis());
				timerMapper.update(t);
			}
		}
		return Result.success(timer);
	}

	@Override
	public List<Timer> list(Long uid, Long startTime, Long endTime) {
		return timerMapper.list(uid, startTime, endTime);
	}

	@Override
	public Result<Timer> searchLastOne(Long uid) {
		return Result.success(timerMapper.searchLastOne(uid));
	}

	@Override
	public Timer selectById(Long id) {
		return timerMapper.select(id);
	}
}

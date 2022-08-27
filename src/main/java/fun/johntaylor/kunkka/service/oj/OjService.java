package fun.johntaylor.kunkka.service.oj;

import java.util.List;

import fun.johntaylor.kunkka.entity.oj.Oj;
import fun.johntaylor.kunkka.utils.result.Result;

/**
 * @Author john
 * @Description
 * @Date 2022/8/26 下午10:18
 */
public interface OjService {

    /**
     * 添加题目
     * 
     * @param oj
     * @return Result<Oj>
     */
    Result<Oj> add(Oj oj);

    /**
     * 更新题目
     * 
     * @param oj
     * @return Result<Oj>
     */
    Result<Oj> update(Oj oj);

    /**
     * 根据uid和创建时间获取题目列表
     * 
     * @param uid
     * @param offset
     * @param count
     * @param begin
     * @param end
     * @return Result
     */
    Result<List<Oj>> searchListByUidTime(Long uid, Integer offset, Integer count, Long begin, Long end);

    /**
     * 根据uid和创建时间获取题目总数
     * 
     * @param uid
     * @param begin
     * @param end
     * @return
     */
    Result<Integer> countByUidTime(Long uid, Long begin, Long end);
}

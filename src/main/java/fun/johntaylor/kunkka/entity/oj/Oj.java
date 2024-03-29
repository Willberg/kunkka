package fun.johntaylor.kunkka.entity.oj;

import javax.validation.constraints.NotNull;

import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.Update;
import lombok.Data;

/**
 * @Author john
 * @Description oj
 * @Date 2022/8/26 下午5:35
 */
@Data
public class Oj {
    /**
     * ID
     */
    @NotNull(message = "请设置ID", groups = {Update.class})
    private Long id;

    /**
     * 题目ID
     */
    @NotNull(message = "请设置题目ID", groups = {Insert.class})
    private Long pid;

    /**
     * uid
     */
    private Long uid;

    /**
     * 题目名字
     */
    @NotNull(message = "请设置题目名字", groups = {Insert.class})
    private String name;

    /**
     * 难度 1-- 简单, 2-- 中等, 3-- 困难
     */
    @NotNull(message = "请设置题目难度", groups = {Insert.class})
    private String difficulty;

    /**
     * 题库 1-- leetcode
     */
    @NotNull(message = "请设置题库", groups = {Insert.class})
    private Integer ojType;

    /**
     * 题目类型
     */
    @NotNull(message = "请设置题目类型", groups = {Insert.class})
    private String type;

    /**
     * 开始或上一次操作的时刻
     */
    @NotNull(message = "请设置开始时间或上一次操作的时刻", groups = {Insert.class, Update.class})
    private Long preTime;

    /**
     * 时长 秒
     */
    private Long useTime;

    /**
     * 是否参考题解 1--是， 2--否
     */
    private String standalone;

    /**
     * 是否学习了题解 1--是， 2--否
     */
    private String study;

    /**
     * 题目链接
     */
    @NotNull(message = "请设置题目链接", groups = {Insert.class})
    private String link;

    /**
     * 题解链接
     */
    private String ansLink;

    /**
     * 重要程度
     */
    @NotNull(message = "请设置题目重要程度", groups = {Insert.class})
    private Integer importance;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 状态， 1-- 开始, 2-- 暂停, 3-- 结束, 4-- 删除
     */
    private Integer status;

    public static final Integer S_BEGIN = 1;
    public static final Integer S_SUSPEND = 2;
    public static final Integer S_END = 3;
    public static final Integer S_DEL = 4;
}

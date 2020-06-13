package fun.johntaylor.kunkka.entity.todo;

import lombok.Data;

@Data
public class TodoList {
    private Long id;

    /**
     * 总价值
     */
    private Integer value;

    /**
     * 完成价值
     */
    private Integer finishValue;

    /**
     * 总用时 单位分钟
     */
    private Integer totalTime;

    /**
     * 最多用时， 不能超过此用时时间
     */
    private Integer maxTime;

    /**
     * 最低优先级，高于此优先级的任务不可过滤
     */
    private Integer minPriority;

    private Long createTime;

    private Long updateTime;

    /**
     * 1-- 待处理， 50- 作废， 100- 完成
     */
    private Integer status;


    public static final Integer MAX_PRIORITY = 10;

    public static final Integer S_PENDING = 1;
    public static final Integer S_DEL = 50;
    public static final Integer S_FINISHED = 100;
}

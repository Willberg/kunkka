package fun.johntaylor.kunkka.entity.todo;

import lombok.Data;

@Data
public class Todo {
    private Long id;

    /**
     * 任务
     */
    private String task;

    /**
     * 价值 1-100
     */
    private Integer value;

    /**
     * 预估时间 单位分钟
     */
    private Integer estimateTime;

    /**
     * 实际用时 单位分钟
     */
    private Integer realityTime;

    private Long listId;

    private Long createTime;

    private Long updateTime;

    /**
     * 权限 1-10， 数值越小，权限越高
     */
    private Integer priority;

    /**
     * 1-- 待处理， 50- 作废， 100- 完成
     */
    private Integer status;

    public static final Integer S_PENDING = 1;
    public static final Integer S_DEL = 50;
    public static final Integer S_FINISHED = 100;
}

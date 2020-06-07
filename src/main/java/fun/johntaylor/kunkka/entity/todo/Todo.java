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

    private Integer priority;

    private Integer status;
}

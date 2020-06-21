package fun.johntaylor.kunkka.entity.todo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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
    @Min(value = 1,message = "不能小于1")
    @Max(value = 100, message = "不能超过10")
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
     * 优先级 1-10， 数值越小，优先级越高
     */
    @Min(value = 1,message = "不能小于1")
    @Max(value = 10, message = "不能超过10")
    private Integer priority;

    /**
     * 1-- 待处理， 50- 作废， 100- 完成
     */
    private Integer status;

    public static final Integer S_PENDING = 1;
    public static final Integer S_DEL = 50;
    public static final Integer S_FINISHED = 100;
}

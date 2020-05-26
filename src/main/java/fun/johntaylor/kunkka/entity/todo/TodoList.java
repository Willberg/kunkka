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

    private Long createTime;

    private Long updateTime;

    /**
     * 1-- 待处理， 99- 作废， 100- 完成
     */
    private Integer status;
}

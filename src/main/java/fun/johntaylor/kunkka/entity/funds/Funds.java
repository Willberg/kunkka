package fun.johntaylor.kunkka.entity.funds;

import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.Update;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author John
 * @Description 收支
 * @Date 2020/8/12 10:02 PM
 **/
@Data
public class Funds {
	/**
	 * ID
	 */
	@NotNull(message = "请设置ID", groups = {Update.class})
	private Long id;

	/**
	 * uid
	 */
	private Long uid;

	/**
	 * 金额
	 */
	@NotNull(message = "请设置金额", groups = {Insert.class})
	private Double amount;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	/**
	 * 	类别， 1--餐饮食物，2--服饰美容，3--交通出行，4--通讯网络，5--医疗保健，6--住房物业，7--图书教育，8--娱乐聚餐
	 * 	101-工资，102-理财，103-项目
	 */
	@NotNull(message = "请设置类别", groups = {Insert.class})
	@Min(value = 1, message = "最小值为1")
	@Max(value = 8, message = "最小值为8")
	private Integer category;

	/**
	 * 类型， 1-- 支出， 2-- 收入
	 */
	@NotNull(message = "请设置类型", groups = {Insert.class})
	@Min(value = 1, message = "最小值为1")
	@Max(value = 2, message = "最小值为2")
	private Integer type;

	/**
	 * 状态，1-- 正常， 0-- 删除
	 */
	private Integer status;

	public static final Integer T_DISBURSEMENT = 1;
	public static final Integer T_INCOME = 2;

	public static final Integer C_FOOD = 1;
	public static final Integer C_CLOTH = 2;
	public static final Integer C_TRAVEL = 3;
	public static final Integer C_CHAT = 4;
	public static final Integer C_HOSPITAL = 5;
	public static final Integer C_LIVE = 6;
	public static final Integer C_BOOK = 7;
	public static final Integer C_ENTERTAINMENT = 8;

	public static final Integer S_NORMAL = 1;
	public static final Integer S_DEL = 2;
}

package fun.johntaylor.kunkka.entity.funds;

import lombok.Data;

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
	private Long id;

	/**
	 * uid
	 */
	private Long uid;

	/**
	 * 金额
	 */
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
	 */
	private Integer category;

	/**
	 * 类型， 1-- 支出， 2-- 收入
	 */
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
}

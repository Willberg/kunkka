package fun.johntaylor.kunkka.service.funds;

import fun.johntaylor.kunkka.entity.funds.Funds;
import fun.johntaylor.kunkka.utils.result.Result;

import java.util.List;

/**
 * @Author John
 * @Description 资金service
 * @Date 2020/8/13 8:53 PM
 **/
public interface FundsService {
	/**
	 * 增加资金记录
	 * @param funds
	 * @return Result<Funds>
	 */
	Result<Funds> add(Funds funds);

	/**
	 * 更新资金记录
	 * @param funds
	 * @return Result<Funds>
	 */
	Result<Funds> update(Funds funds);

	/**
	 * 根据type查询资金流水
	 * @param type
	 * @param uid
	 * @param startTime
	 * @param endTime
	 * @return List<Funds>
	 */
	List<Funds> listFunds(Integer type, Long uid, Long startTime, Long endTime);

	/**
	 * 查询资金流水
	 * @param uid
	 * @param startTime
	 * @param endTime
	 * @return List<Funds>
	 */
	List<Funds> list(Long uid, Long startTime, Long endTime);

	/**
	 * 查询资金流水
	 * param id
	 * @return Fund
	 */
	Funds search(Long id);
}

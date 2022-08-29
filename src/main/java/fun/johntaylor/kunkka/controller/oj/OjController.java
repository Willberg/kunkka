package fun.johntaylor.kunkka.controller.oj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import fun.johntaylor.kunkka.component.redis.session.Session;
import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.oj.Oj;
import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.Update;
import fun.johntaylor.kunkka.service.oj.OjService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @Author john
 * @Description 题库需求
 * @Date 2022/8/26 下午10:15
 */
@RestController
@Slf4j
public class OjController {
    @Autowired
    private DbThreadPool dbThreadPool;

    @Autowired
    private OjService ojService;

    @Autowired
    private Session session;

    @PostMapping(value = "/api/oj/add")
    public Mono<String> add(ServerHttpRequest request, @Validated(value = {Insert.class}) @RequestBody Oj oj) {
        return Mono.just(session.getUser(request)).publishOn(dbThreadPool.daoInstance()).map(v -> {
            oj.setUid(v.getId());
            oj.setStandalone(Oj.SE_NO);
            oj.setStudy(Oj.ST_NO);
            oj.setUseTime(0L);
            oj.setCreateTime(oj.getPreTime());
            oj.setUpdateTime(oj.getPreTime());
            oj.setStatus(Oj.S_BEGIN);
            return ojService.add(oj).toString();
        });
    }

    @PostMapping(value = "/api/oj/update")
    public Mono<String> update(ServerHttpRequest request, @Validated(value = {Update.class}) @RequestBody Oj oj) {
        return Mono.just(session.getUser(request)).publishOn(dbThreadPool.daoInstance()).map(v -> {
            oj.setUpdateTime(System.currentTimeMillis());
            return ojService.update(oj).toString();
        });
    }

    @GetMapping(value = "/api/oj/list")
    public Mono<String> list(ServerHttpRequest request,
        @RequestParam(value = "offset", defaultValue = "0") Integer offset,
        @RequestParam(value = "count", defaultValue = "10") Integer count,
        @RequestParam(value = "begin", required = false) Long begin,
        @RequestParam(value = "end", required = false) Long end) {
        return Mono.just(session.getUser(request)).publishOn(dbThreadPool.daoInstance())
            .map(v -> ojService.searchListByUidTime(v.getId(), offset, count, begin, end).toString());
    }

    @GetMapping(value = "/api/oj/count")
    public Mono<String> count(ServerHttpRequest request, @RequestParam(value = "begin", required = false) Long begin,
        @RequestParam(value = "end", required = false) Long end) {
        return Mono.just(session.getUser(request)).publishOn(dbThreadPool.daoInstance())
            .map(v -> ojService.countByUidTime(v.getId(), begin, end).toString());
    }
}

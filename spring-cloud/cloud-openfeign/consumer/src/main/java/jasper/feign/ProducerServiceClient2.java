package jasper.feign;

import com.jasper.result.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 基于 SpringContract 拓展契约
 */
public interface ProducerServiceClient2{

    @GetMapping("/producer")
    ApiResult<String> index();

    @GetMapping("/producer/test")
    ApiResult<String> test();

    @GetMapping("/producer/echo/{string}")
    ApiResult<String> echo(
            @PathVariable("string") String string
    );
}

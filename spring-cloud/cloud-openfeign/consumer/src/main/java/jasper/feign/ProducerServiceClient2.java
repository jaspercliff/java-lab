package jasper.feign;

import com.jasper.result.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 基于 SpringContract 拓展契约
 */
public interface ProducerServiceClient2{

    @GetMapping("/producer")
    ApiResponse<String> index();

    @GetMapping("/producer/test")
    ApiResponse<String> test();

    @GetMapping("/producer/echo/{string}")
    ApiResponse<String> echo(
            @PathVariable("string") String string
    );
}

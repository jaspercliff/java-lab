package jasper.feign;

import com.jasper.result.ApiResult;
import feign.Param;
import feign.RequestLine;

public interface ProducerServiceClient1 {

    @RequestLine("GET /producer")
    ApiResult<String> index();

    @RequestLine("GET /producer/test")
    ApiResult<String> test();

    @RequestLine("GET /producer/echo/{string}")
    ApiResult<String> echo(@Param("string") String string);
}
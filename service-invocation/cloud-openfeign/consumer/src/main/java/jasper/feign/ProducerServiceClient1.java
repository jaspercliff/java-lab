package jasper.feign;

import com.jasper.result.ApiResponse;
import feign.Param;
import feign.RequestLine;

public interface ProducerServiceClient1 {

    @RequestLine("GET /producer")
    ApiResponse<String> index();

    @RequestLine("GET /producer/test")
    ApiResponse<String> test();

    @RequestLine("GET /producer/echo/{string}")
    ApiResponse<String> echo(@Param("string") String string);
}
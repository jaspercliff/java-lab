package jasper.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldDifference {
    private String tableName;
    private String fieldName;
    private Object oldValue;
    private Object newValue;

}

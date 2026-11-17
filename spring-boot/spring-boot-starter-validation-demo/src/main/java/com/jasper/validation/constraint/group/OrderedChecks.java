package com.jasper.validation.constraint.group;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

/**
 * 知识点：组序列（GroupSequence）——控制校验顺序，实现「先粗后细」的分阶段校验。
 *
 * <p>{@code @Validated(OrderedChecks.class)} 时按声明顺序依次执行：
 * {@link Default}（非空/长度等基础约束） → {@link CreateGroup}（格式/业务约束）。
 * 前一组有错误时后一组不再执行，避免用户还没填就报一堆格式错。
 *
 * <p>注意：组序列里不能出现形成循环的组，且不能把 {@code Default} 之外的组再反向依赖回来。
 */
@GroupSequence({CreateGroup.class, Default.class, UpdateGroup.class})
public interface OrderedChecks {
}

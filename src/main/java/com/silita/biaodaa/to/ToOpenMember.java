package com.silita.biaodaa.to;

import com.silita.biaodaa.model.TbVipFeeStandard;
import lombok.Getter;
import lombok.Setter;

/**
 * 开通会员 to
 */
@Setter
@Getter
public class ToOpenMember {
    private String channel;

    private String userId;

    private TbVipFeeStandard feeStandard;
}

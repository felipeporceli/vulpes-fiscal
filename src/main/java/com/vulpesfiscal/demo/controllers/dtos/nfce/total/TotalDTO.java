package com.vulpesfiscal.demo.controllers.dtos.nfce.total;

import com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.IBSCBSTotDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.icmstot.ICMSTotDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.issqntot.ISSQNtotDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.istot.IsTotDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.rettrib.RetTribDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TotalDTO {

    private ICMSTotDTO icmsTot;
    private ISSQNtotDTO issqNtot;
    private RetTribDTO retTrib;
    private IsTotDTO isTot;
    private IBSCBSTotDTO ibscbsTot;
    private BigDecimal vNFTot;

}

package com.vulpesfiscal.demo.controllers.dtos.nfce.det.obsitem;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.obsitem.obscont.ObsContDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.obsitem.obsfisco.ObsFiscoDTO;
import lombok.Data;

@Data
public class ObsItemDTO {

    private ObsContDTO obsCont = null;
    private ObsFiscoDTO obsFisco = null;

}

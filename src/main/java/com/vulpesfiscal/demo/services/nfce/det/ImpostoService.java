package com.vulpesfiscal.demo.services.nfce.det;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.II.IIDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins.COFINSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofinsst.COFINSSTDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.IBSCBSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icms.ICMSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ImpostoDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icmsufdest.ICMSUFDestDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ipi.IPIDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.is.ISDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.issqn.ISSQNDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pis.PISDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pisst.PISSTDTO;
import org.springframework.stereotype.Service;

@Service
public class ImpostoService {
    public ImpostoDTO gerarImposto () {
        ImpostoDTO imposto = new ImpostoDTO();
        ICMSDTO icms = new ICMSDTO();


        // Gerar ICMS
        icms.setIcms00DTO(null);
        icms.setIcms10DTO(null);
        icms.setIcms15DTO(null);
        icms.setIcms20DTO(null);
        icms.setIcms30DTO(null);
        icms.setIcms40DTO(null);
        icms.setIcms51DTO(null);
        icms.setIcms53DTO(null);
        icms.setIcms60DTO(null);
        icms.setIcms61DTO(null);
        icms.setIcms70DTO(null);
        icms.setIcms90DTO(null);
        icms.setIcmsPartDTO(null);
        icms.setIcmssn101DTO(null);
        icms.setIcmssn102DTO(null);
        icms.setIcmssn201DTO(null);
        icms.setIcmssn202DTO(null);
        icms.setIcmssn500DTO(null);
        icms.setIcmssn900DTO(null);
        icms.setIcmsstdto(null);

        // Gerar IPI
        IPIDTO ipi = new IPIDTO();
        ipi.setCEnq(null);
        ipi.setCnpjProd(null);
        ipi.setQSelo(null);
        ipi.setCSelo(null);
        ipi.setIpiTribDTO(null);
        ipi.setIpIntDTO(null);

        // Gerar imposto sobre importação
        IIDTO ii = new IIDTO();
        ii.setVBC(null);
        ii.setVII(null);
        ii.setVDespAdu(null);
        ii.setVII(null);

        // Gerar ISSQN
        ISSQNDTO issqn = new ISSQNDTO();
        issqn.setCListServ(null);
        issqn.setVBC(null);
        issqn.setVAliq(null);
        issqn.setVISSQN(null);
        issqn.setCMunFG(null);
        issqn.setCListServ(null);
        issqn.setVDeducao(null);
        issqn.setVOutro(null);
        issqn.setVDescIncond(null);
        issqn.setVDescCond(null);
        issqn.setVISSRet(null);
        issqn.setIndISS(null);
        issqn.setCServico(null);
        issqn.setCMun(null);
        issqn.setCPais(null);
        issqn.setNProcesso(null);
        issqn.setIndIncentivo(null);

        // Gerar PIS
        PISDTO pis = new PISDTO();
        pis.setPisAliq(null);
        pis.setPisOutr(null);
        pis.setPisnt(null);
        pis.setPisQtde(null);

        // Gerar PISST
        PISSTDTO pisst = new PISSTDTO();
        pisst.setVBC(null);
        pisst.setPPIS(null);
        pisst.setQBCProd(null);
        pisst.setVAliqProd(null);
        pisst.setVPIS(null);
        pisst.setIndSomaPISST(null);

        // Gerar COFINS
        COFINSDTO cofins = new COFINSDTO();
        cofins.setCofinsAliq(null);
        cofins.setCofinsnt(null);
        cofins.setCofinsOutr(null);
        cofins.setCofinsQtde(null);

        // Gerar COFINSST
        COFINSSTDTO cofinsst = new COFINSSTDTO();
        cofinsst.setVBC(null);
        cofinsst.setPCOFINS(null);
        cofinsst.setQBCProd(null);
        cofinsst.setVAliqProd(null);
        cofinsst.setVCOFINS(null);
        cofinsst.setIndSomaCOFINSST(null);

        // Gerar ICMSUFDEST
        ICMSUFDestDTO icmsufDest = new ICMSUFDestDTO();
        icmsufDest.setVBCUFDest(null);
        icmsufDest.setVBCFCPUFDest(null);
        icmsufDest.setPFCPUFDest(null);
        icmsufDest.setPICMSUFDest(null);
        icmsufDest.setPICMSInter(null);
        icmsufDest.setPICMSInterPart(null);
        icmsufDest.setVFCPUFDest(null);
        icmsufDest.setVICMSUFDest(null);
        icmsufDest.setVICMSUFRemet(null);

        // Gerar IS
        ISDTO is = new ISDTO();
        is.setCSTIS(null);
        is.setCClassTribIS(null);
        is.setVBCIS(null);
        is.setPIS(null);
        is.setPISEspec(null);
        is.setUTrib(null);
        is.setQTrib(null);
        is.setVIS(null);

        // Gerar IBS/CBS
        IBSCBSDTO ibscbs = new IBSCBSDTO();
        ibscbs.setCst(null);
        ibscbs.setCClassTrib(null);
        ibscbs.setIndDoacao(null);
        ibscbs.setGIBSCSB(null);
        ibscbs.setGibscbsMono(null);
        ibscbs.setGTransfCred(null);
        ibscbs.setGAjusteCompet(null);
        ibscbs.setGEstornoCredDTO(null);
        ibscbs.setGCredPresOper(null);
        ibscbs.setGCredPresIBSZFM(null);


        imposto.setIcms(icms);
        imposto.setIpi(ipi);
        imposto.setIi(ii);
        imposto.setIssqn(issqn);
        imposto.setPis(pis);
        imposto.setPisst(pisst);
        imposto.setCofins(cofins);
        imposto.setCofinsst(cofinsst);
        imposto.setIcmsufDest(icmsufDest);
        imposto.setIs(is);
        imposto.setIbscbs(ibscbs);
        return imposto;
    }
}

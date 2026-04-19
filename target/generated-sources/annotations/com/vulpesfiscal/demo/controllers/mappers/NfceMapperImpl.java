package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.nfce.AutXMLDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.InfNFe;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.DetDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T00:22:50-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class NfceMapperImpl implements NfceMapper {

    @Override
    public InfNFe toEntity(InfNFe dto) {
        if ( dto == null ) {
            return null;
        }

        InfNFe infNFe = new InfNFe();

        infNFe.setVersao( dto.getVersao() );
        infNFe.setId( dto.getId() );
        infNFe.setIde( dto.getIde() );
        infNFe.setEmit( dto.getEmit() );
        infNFe.setDest( dto.getDest() );
        List<DetDTO> list = dto.getDet();
        if ( list != null ) {
            infNFe.setDet( new ArrayList<DetDTO>( list ) );
        }
        infNFe.setImposto( dto.getImposto() );
        infNFe.setTotal( dto.getTotal() );
        infNFe.setTransporte( dto.getTransporte() );
        infNFe.setPagamento( dto.getPagamento() );
        infNFe.setAvulsa( dto.getAvulsa() );
        infNFe.setRetirada( dto.getRetirada() );
        infNFe.setEntrega( dto.getEntrega() );
        List<AutXMLDTO> list1 = dto.getAutXML();
        if ( list1 != null ) {
            infNFe.setAutXML( new ArrayList<AutXMLDTO>( list1 ) );
        }

        return infNFe;
    }
}

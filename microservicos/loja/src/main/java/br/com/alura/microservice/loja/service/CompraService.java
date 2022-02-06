package br.com.alura.microservice.loja.service;

import br.com.alura.microservice.loja.client.TransportadorClient;
import br.com.alura.microservice.loja.dto.*;
import br.com.alura.microservice.loja.model.CompraState;
import br.com.alura.microservice.loja.repository.CompraRespository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.microservice.loja.client.FornecedorClient;
import br.com.alura.microservice.loja.model.Compra;

import java.time.LocalDate;

@Service
public class CompraService {

    @Autowired
    private FornecedorClient fornecedorClient;

    @Autowired
    private TransportadorClient transportadorClient;

    @Autowired
    private CompraRespository compraRespository;

    @HystrixCommand(fallbackMethod = "realizaCompraFallback")
    public Compra realizaCompra(CompraDTO compra) {

        final String estado = compra.getEndereco().getEstado();

        Compra compraSalva = new Compra();
        compraSalva.setEnderecoDestino(compra.getEndereco().toString());
        compraSalva.setState(CompraState.RECEBIDO);
        compraRespository.save(compraSalva);
        compra.setCompraId(compraSalva.getId());


        InfoFornecedorDTO info = fornecedorClient.getInfoPorEstado(estado);
        InfoPedidoDTO infoPedido = fornecedorClient.realizaPedido(compra.getItens());
        compraSalva.setPedidoId(infoPedido.getId());
        compraSalva.setTempoDePreparo(infoPedido.getTempoDePreparo());
        compraSalva.setState(CompraState.PEDIDO_REALIZADO);
        compraRespository.save(compraSalva);

        InfoEntregaDTO entregaDTO = new InfoEntregaDTO();
        entregaDTO.setPedidoId(infoPedido.getId());
        entregaDTO.setDataParaEntrega(LocalDate.now().plusDays(infoPedido.getTempoDePreparo()));
        entregaDTO.setEnderecoOrigem(info.getEndereco());
        entregaDTO.setEnderecoDestino(compra.getEndereco().toString());
        VoucherDTO voucher = transportadorClient.reservaEntrega(entregaDTO);
        compraSalva.setDataParaEntrega(voucher.getPrevisaoParaEntrega());
        compraSalva.setVoucher(voucher.getNumero());
        compraSalva.setState(CompraState.RESERVA_ENTREGA_REALIZADA);
        compraRespository.save(compraSalva);

        return compraSalva;
    }

    public Compra realizaCompraFallback(CompraDTO compra) {

        if (compra.getCompraId() != null){
            return compraRespository.findById(compra.getCompraId()).get();
        }
        Compra compraFallback = new Compra();
        compraFallback.setEnderecoDestino(compra.getEndereco().toString());


        return compraFallback;
    }

    @HystrixCommand
    public Compra getById(Long id) {
        return compraRespository.findById(id).orElse(new Compra());
    }

    public Compra reprocessaCompra(Long id){
        return null;
    }

}

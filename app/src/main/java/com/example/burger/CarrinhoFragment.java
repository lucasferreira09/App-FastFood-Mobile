package com.example.burger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.burger.HoldersEAdapters.AdapterLanchesCarrinho;
import com.example.burger.interfaces.ListenerTextView;

import java.util.List;

public class CarrinhoFragment extends Fragment implements ListenerTextView {

    //DADOS DO USUÁRIO - NOME E ENDEREÇO
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_DADOS = "myDados";
    private static final String KEY_NOME = "nome";
    private static final String KEY_RUA = "rua";
    private static final String KEY_NUMERO = "numero";
    private static final String KEY_BAIRRO = "bairro";


    private TextView valorPedidoTotal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_carrinho, container, false);

        sharedPreferences = getContext().getSharedPreferences(SHARED_PREF_DADOS, Context.MODE_PRIVATE);


        valorPedidoTotal = view.findViewById(R.id.valorPedidoTotal);
        ImageButton btnPedir = view.findViewById(R.id.btnPedir);
        ImageButton btnVoltar = view.findViewById(R.id.btnVoltar);
        ImageView btnVerEndereco = view.findViewById(R.id.btnVerEndereco);
        ConstraintLayout telaCarrinho = view.findViewById(R.id.telaCarrinho);
        RecyclerView recyclerCarrinho = view.findViewById(R.id.recyclerCarrinho);

        //Será usada para pegar toda a lista de Lanches adicionados
        Burgueria burgueria = new Burgueria();

        String pedidoTotal = calculaPedidoTotal(burgueria.getListaGeral());
        valorPedidoTotal.setText(pedidoTotal);

        //Prepara a lista que terá todos os pedidos adicionados
        Context context = getActivity();
        AdapterLanchesCarrinho adapterLanchesCarrinho = new AdapterLanchesCarrinho(burgueria.getListaGeral(), context, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recyclerCarrinho.setAdapter(adapterLanchesCarrinho);
        recyclerCarrinho.setLayoutManager(layoutManager);


        //Botão de fazer o pedido. Será redirecionado para WhatsApp
        btnPedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enviarPedido = new Intent();
                enviarPedido.setAction(Intent.ACTION_SEND);
                enviarPedido.putExtra(Intent.EXTRA_TEXT, descricaoPedidoTotal(burgueria.getListaGeral()));
                enviarPedido.setType("text/plain");

                Intent shareIntent = Intent.createChooser(enviarPedido, null);
                startActivity(shareIntent);
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        //Botão de ir para o Endereço
        btnVerEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerEndereco verEndereco = new VerEndereco();

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.telaCarrinho, verEndereco);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    //Método da interface para mudar o $ Valor total do pedido
    @Override
    public void clickTextView(List<Burgueria> listaBurgueria) {
        String pedidoTotal = calculaPedidoTotal(listaBurgueria);
        valorPedidoTotal.setText("$ " + pedidoTotal);
    }

    //Calcula o $ Valor total do pedido
    public String calculaPedidoTotal(List<Burgueria> lista){
        int novoValorTotal = 0;

        for (Burgueria b : lista){
            int valor = b.getQuantidLanche() * b.getPriceLanche();
            novoValorTotal += valor;
        }
        return String.valueOf(novoValorTotal);
    }


    //Faz uma descrição do pedido para enviar por WhatsApp
    public String descricaoPedidoTotal(List<Burgueria> lista){
        StringBuilder descricaoPedido = new StringBuilder();
        descricaoPedido.append("Oii, tudo bem? \uD83D\uDE0B \uD83C\uDFDA️ Vou querer:\n");
        descricaoPedido.append("\n");

        //Obtêm quais lanches foram adicionados ao carrinho
        for (Burgueria burgueria : lista){
            if (burgueria.getQuantidLanche() != 0){
                String nomeLanche = burgueria.getNameLanche();
                String quantidade = String.valueOf(burgueria.getQuantidLanche());
                descricaoPedido.append(quantidade + "* " + nomeLanche + "\n");
            }
        }
        descricaoPedido.append("\n");
        descricaoPedido.append(obtemDadosUsuario());

        return descricaoPedido.toString();
    }

    //Pega os dados do usuário para fazer o pedido
    public String obtemDadosUsuario() {

        StringBuilder dados = new StringBuilder();

        dados.append("\uD83C\uDFDA Endereço\n");
        dados.append("Lucas");
        dados.append("Rua " + "José Barbosa");
        dados.append("\nN° " + "167");
        dados.append("\nBairro  " +  "São Francisco");

        return dados.toString();


    }
}
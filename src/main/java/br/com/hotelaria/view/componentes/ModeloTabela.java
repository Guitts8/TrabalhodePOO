package br.com.hotelaria.view.componentes;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModeloTabela<T> extends AbstractTableModel {
    private final List<String> nomes = new ArrayList<>();
    private final List<Function<T, Object>> valores = new ArrayList<>();
    private List<T> linhas = new ArrayList<>();

    public ModeloTabela<T> coluna(String nome, Function<T, Object> valor) {
        nomes.add(nome);
        valores.add(valor);
        return this;
    }

    public void setLinhas(List<T> novasLinhas) {
        linhas = new ArrayList<>(novasLinhas);
        fireTableDataChanged();
    }

    public List<T> getLinhas() {
        return linhas;
    }

    public T getLinha(int indice) {
        return linhas.get(indice);
    }

    @Override
    public int getRowCount() {
        return linhas.size();
    }

    @Override
    public int getColumnCount() {
        return nomes.size();
    }

    @Override
    public String getColumnName(int coluna) {
        return nomes.get(coluna);
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        return valores.get(coluna).apply(linhas.get(linha));
    }
}

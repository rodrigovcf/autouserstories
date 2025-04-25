package com.autous.autouserstories.calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FunctionPointCalculator {

    // Classe para representar os critérios
    public static class FunctionPointCriteria {
        private final String status;
        private final String guideline;
        private final String functionType;

        public FunctionPointCriteria(String status, String guideline, String functionType) {
            this.status = status;
            this.guideline = guideline;
            this.functionType = functionType;
        }

        // Sobrescrevendo equals e hashCode para uso no Map
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FunctionPointCriteria that = (FunctionPointCriteria) o;
            return Objects.equals(status, that.status) &&
                    Objects.equals(guideline, that.guideline) &&
                    Objects.equals(functionType, that.functionType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(status, guideline, functionType);
        }
    }

    // Mapa para armazenar os critérios e seus valores
    private final Map<FunctionPointCriteria, Double> criteriaMap = new HashMap<>();

    public FunctionPointCalculator() {
        // Inicializar o mapa com os critérios e valores
        criteriaMap.put(new FunctionPointCriteria("eliminar", "base de dados", "requisito funcional"), 2.16);
        criteriaMap.put(new FunctionPointCriteria("modificar", "base de dados", "requisito funcional"), 3.03);
        criteriaMap.put(new FunctionPointCriteria("criar", "base de dados", "requisito funcional"), 7.21);
        criteriaMap.put(new FunctionPointCriteria("eliminar", "processo elementar", "requisito funcional"), 1.66);
        criteriaMap.put(new FunctionPointCriteria("modificar", "processo elementar", "requisito funcional"), 3.31);
        criteriaMap.put(new FunctionPointCriteria("criar", "processo elementar", "requisito funcional"), 5.52);
        // Adicione os demais critérios aqui...
    }

    // Método para buscar o valor com base nos critérios
    public double getFunctionPointValue(String status, String guideline, String functionType) {
        FunctionPointCriteria criteria = new FunctionPointCriteria(status, guideline, functionType);
        return criteriaMap.getOrDefault(criteria, 0.0); // Retorna 0.0 se o critério não for encontrado
    }
}
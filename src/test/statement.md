# Problema de asignación de recursos

Sea un grupo de N personas que tienen que ir a cenar y un conjunto de M restaurantes, cada restaurante tiene una capacidad de recepción C_i, de modo que la capacidad total de los M restaurantes es mayor que 2*N, pero cada C_i es menor que N. 

Todos los restaurantes están ocupados en horarios normales, con una cierta probabilidad de estar llenos. 
La probabilidad de encontrar p lugares en un restaurante con capacidad C_i es la misma que la de encontrar 2p lugares en un restaurante con capacidad 2*C_i. 

Todas las personas conocen la capacidad de cada restaurante gracias al folleto turístico que tienen en su poder. 
Evaluar los resultados (número de llamadas telefónicas requeridas según los agentes), según M, N y C_i, para los comportamientos propuestos.
El principio es que cada agente reserva un restaurante y vuelve a empezar en caso de no conseguirlo.

Por ejemplo, un agente llama a un restaurante, pero este ya fue ocupado totalmente en su capacidad C_i, entonces el agente debe volver a llamar a otro restaurante para buscar lugar.

Se trata de evaluar varios comportamientos posibles.
Puede ilustrar su respuesta con un ejemplo.

## Pregunta 1
Comportamiento aleatorio. Cada persona elige un restaurante al azar entre los M posibles:
- Qué resultaría en este caso, ¿el hecho de que cada agente sepa que todos los demás razonan como él?

## Pregunta 2
Iteración temporal: Cada persona elige un restaurante en función del resultado del día anterior, es decir, siempre elige el restaurante donde cenó la noche anterior. 
- ¿Existe algún comportamiento que garantice obtener una reserva a la primera vez? ¿Bajo qué supuestos? ¿Cuál es su desventaja?

## Pregunta 3
Comportamiento "codicioso": Cada persona elige para maximizar la probabilidad de encontrar un lugar. Se calcula la probabilidad de obtener mesa y se elige el lugar más probable.
- ¿Qué resultaría en este caso el hecho de que cada agente sepa que todos los demás razonan como él?

## Pregunta 4
Deliberación: Cada agente hace una primera elección (por ejemplo, según el criterio de la pregunta 3), pregunta a los agentes P (elegidos al azar) qué hacen y corrige su elección. Consideraremos que la elección de P agentes es una encuesta representativa de la elección de N agentes.
Esto equivale a considerar que cada agente pregunta a los demás qué están haciendo y elige según esto. 
- ¿Cuál es el resultado de este comportamiento?

## Pregunta 5
Deliberación iterativa: Si consideramos que puede haber varias iteraciones de estas deliberaciones, ¿cuál es el resultado de un comportamiento con 2, luego 3 iteraciones?
- ¿Podemos predecir el resultado asintótico cuando el número de iteraciones aumenta indefinidamente?

## Pregunta 6
Se requiere que programe un sistema multiagente en JADE que genere N=20 agentes y M=6 restaurantes con capacidades (número de mesas) C={10,10,15,15,5,5} respectivamente que:

- Tenga 1 agente host que inicializa la cantidad de agentes y restaurantes, y cada minuto genere una "nueva noche" para que los agentes elijan lugares para cenar. 
- Tenga 5 agentes persona con comportamiento aleatorio (pregunta 1), eligen un restaurante aleatoriamente para cada noche.
- Tenga 5 agentes persona con comportamiento iterativo (pregunta 2), la primera noche eligen aleatoriamente y las subsecuentes repiten esa elección. 
- Tenga 5 agentes persona con que estiman una probabilidad (pregunta 3) a partir de una deliberación (pregunta 4), antes de elegir hacen una encuesta a los otros agentes y elige el lugar menos concurrido.
- Tenga 5 agentes que deliberan reiterativamente (pregunta 5), es decir luego de una primera consulta, vuelven a consultar para asegurar o cambiar su elección.

## Representación
```
N: # personas
M: # restaurantes

R = [r1, r2, r3, ..., rm] // restaurantes
C = [c1, c2, c3, ..., cm] // capacidad
V = [v1, v2, v3, ..., vm] // lugares vacios
P = [p1, p2, p3, ..., pm] // probabilidad de agarrar un cupo

sum(C) > 2*N
C[i] < N
(C[i] == C[i] && P[i] = p) == (C[j] == 2*C[i] && P[j] = 2*p)

// Número de llamadas telefónicas
// Cada agente reserva un restaurante y vuelve a empezar en caso de no conseguirlo
```



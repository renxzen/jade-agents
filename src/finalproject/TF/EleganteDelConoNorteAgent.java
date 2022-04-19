/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.TF;

import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;

import finalproject.agents.EntityAgent;
import finalproject.agents.FieldAgent;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import finalproject.environment.Functions;
import finalproject.frame.EnvironmentPanel;

import jade.lang.acl.ACLMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

/**
 * @author G10: Ferroa & Espiritu
 */
public class EleganteDelConoNorteAgent extends FieldAgent {
    private boolean crack = true;
    private boolean modo_ruso = false;
    private Timer timer_1;
    private Timer timer_2;
    private Timer timer_3;
    private TimerTask _timerTask;
    private ScheduledExecutorService executor = null;
    private TimerTask task;
    protected ScheduledExecutorService executor_1 = null;

    protected void mensajes() {
        FieldUnity enemy = detectEnemies();
        if (enemy != null) sendMessageAllAllies(ENEMY, enemy);
        // status = STATES.EATING;
        FoodSource source = detectSources();
        if (source != null) sendMessageAllAllies(SOURCE, source);
    }

    protected void tortuga() {
        FieldUnity unidad_local = getLocal();
        for (FieldUnity ally : unidad_local.species.members.values()) {
            if (!ally.equals(unidad_local)
                    && (ally.position.getX() > 0
                            && ally.position.getX() <= EnvironmentPanel.MAX_X / 2)
                    && (ally.position.getY() > 0
                            && ally.position.getY() <= EnvironmentPanel.MAX_Y / 2)) {
                getLocal().setGoal(ally.position);
                status = STATES.GOINGTOFIGHT;
                status = STATES.PATROLING;

                mensajes();
            }
            mensajes();
        }
        mensajes();
    }

    protected void ruso() {
        FieldUnity unidad_local = getLocal();
        for (FieldUnity ally : unidad_local.species.members.values()) {
            if (!ally.equals(unidad_local)) {
                getLocal().setGoal(Functions.randomPosition());
                status = STATES.GOINGTOFIGHT;
                status = STATES.PATROLING;

                mensajes();
            }
            mensajes();
        }
        FieldUnity enemy = detectEnemies();
        if (enemy != null) {
            sendMessageAllAllies(ENEMY, enemy);
            crack = true;
        }
        // status = STATES.EATING;
        FoodSource source = detectSources();
        if (source != null) sendMessageAllAllies(SOURCE, source);
    }

    ////////////////////////////////////////////////
    //////////////// MOVIIMIENTOS///////////////////
    //////////////////////////////////////////////

    @Override
    protected void patrol() {
        super.init();
        super.patrol();

        if (crack == true) {
            tortuga();
        }
        if (modo_ruso == true) {
            ruso();
        }

        mensajes();
    }

    ///////////////////////////////////////////////
    ////////////// MENSAJERIA//////////////////////
    //////////////////////////////////////////////

    @Override
    protected void computeMessage(ACLMessage msg) {
        super.init();
        if (status == STATES.PATROLING) {
            if (msg.getContent().contains(ENEMY + "_")) {
                System.out.println(msg.getContent());
                String[] m = msg.getContent().split("_");
                FieldUnity enemy = FieldUnity.ALL_UNITIES.get("entity_" + m[1]);
                if (enemy != null) {
                    if (getLocal().position.distance(enemy.position) < 500) {
                        getLocal().setGoal(enemy.position);
                        status = STATES.GOINGTOFIGHT;
                        getLocal().goal_enemy = enemy;
                    }
                }
            } else if (msg.getContent().contains(SOURCE + "_")) {
                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);
                if (source != null) {
                    crack = false;
                    if (getLocal().position.distance(source.position) < 100000) {
                        getLocal().setGoal(source.position);
                        status = EntityAgent.STATES.GOINGTOSOURCE;
                    }
                }
            }
        }
    }

    //////////////////////////////////////////////
    /////////////// TIMERS/////////////////////////
    //////////////////////////////////////////////

    public TimerTask tiempoEjecucion() {
        _timerTask =
                new TimerTask() {
                    int count = 1;

                    @Override
                    public void run() {
                        status = STATES.REINFORCING;
                        if (count >= 15) {
                            executor.shutdown();
                        }
                        count++;
                    }
                };
        return _timerTask;
    }

    public TimerTask tarea() {
        task =
                new TimerTask() {
                    int count = 1;

                    @Override
                    public void run() {
                        modo_ruso = true;
                        if (count >= 10) {
                            modo_ruso = false;
                            crack = true;
                            executor_1.shutdown();
                        }
                        count++;
                    }
                };
        return task;
    }

    protected void init() {
        super.init();
        if (_timerTask == null) {
            executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(tiempoEjecucion(), 1, 1, TimeUnit.SECONDS);
        }
        if (timer_1 == null) {
            timer_1 =
                    new Timer(
                            20000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    if (getLocal().species.getStock() > 60) {
                                        status = STATES.REPRODUCING;
                                    }
                                }
                            });
            timer_1.start();
        }
        if (timer_2 == null) {
            timer_2 =
                    new Timer(
                            20000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    if (getLocal().species.getStock() >= 40
                                            && getLocal().species.getStock() <= 60) {
                                        status = STATES.REINFORCING;
                                    }
                                }
                            });
            timer_2.start();
        }
        if (timer_3 == null) {
            timer_3 =
                    new Timer(
                            20000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    if (crack == true) {
                                        crack = false;
                                    } else {
                                        crack = true;
                                    }
                                }
                            });
            timer_3.start();
        }
        if (task == null || executor_1.isShutdown()) {
            executor_1 = Executors.newScheduledThreadPool(1);
            executor_1.scheduleAtFixedRate(tarea(), 20, 1, TimeUnit.SECONDS);
        }
    }
}

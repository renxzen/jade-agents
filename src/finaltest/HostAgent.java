package finaltest;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

import java.util.ArrayList;
import java.util.List;

public class HostAgent extends Agent {
    public static final Integer[] capacities = {5, 3, 2, 3, 2, 5};
    public static final Integer numRestaurants = 6;
    public static final Integer numRandom = 5;
    public static final Integer numIterative = 5;
    public static final Integer numProbability = 5;
    public static final Integer numReiterative = 5;
    public static final Integer totalPersons = numRandom + numIterative + numProbability;
    private Integer nightIndex = 1;
    private List<String> restaurants = new ArrayList<String>();
    private List<String> persons = new ArrayList<String>();

    private AgentController agentController;
    private PlatformController container;

    @Override
    public void setup() {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            DFService.register(this, dfd);
            container = getContainerController();

            for (int i = 0; i < numRestaurants; i++)
                createAgent("Restaurant_" + (i + 1), "test.RestaurantAgent");

            for (int i = 0; i < numRandom; i++)
                createAgent("Person_" + (i + 1), "test.RandomAgent");

            for (int i = 0; i < numIterative; i++)
                createAgent("Person_" + (numRandom + i + 1), "test.IterativeAgent");

            for (int i = 0; i < numReiterative; i++)
                createAgent(
                        "Person_" + (numRandom + numIterative + i + 1), "test.ProbabilityAgent");

            // for (int i = 0; i < numRestaurants; i++)
            // 	createAgent("Person_" + (numRandom + numIterative + numProbability + i + 1),
            // "test.ReierativeAgent");

            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.setContent("START_POLL");

            for (String person : persons) message.addReceiver(new AID(person, AID.ISLOCALNAME));
            send(message);

            ParallelBehaviour parallel = new ParallelBehaviour();

            parallel.addSubBehaviour(
                    new TickerBehaviour(this, 20000) {
                        @Override
                        public void onTick() {
                            nightIndex++;
                            System.out.println(
                                    String.format("\n[Night %d] Has started.", nightIndex));

                            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                            message.setContent("NEW_NIGHT");

                            for (String restaurant : restaurants)
                                message.addReceiver(new AID(restaurant, AID.ISLOCALNAME));

                            for (String person : persons)
                                message.addReceiver(new AID(person, AID.ISLOCALNAME));

                            send(message);
                        }
                    });

            addBehaviour(parallel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAgent(String agentName, String classi) throws Exception {
        agentController = container.createNewAgent(agentName, classi, null);
        agentController.start();

        if (agentName.startsWith("Person_")) persons.add(agentName);
        if (agentName.startsWith("Restaurant_")) restaurants.add(agentName);
    }
}

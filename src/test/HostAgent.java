package test;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;
import jade.lang.acl.ACLMessage;

public class HostAgent extends Agent {
	public static final Integer[] capacities = { 10, 10, 15, 15, 5, 5 };
	public static final Integer numRestaurants = 6;
	public static final Integer numRandom = 5;
	public static final Integer numIterative = 5;
	public static final Integer numProbability = 5;
	public static final Integer numReiterative = 5;
	public static final Integer totalPersons = numRandom + numIterative + numProbability;
	private Integer nightIndex = 1;
	private List<String> restaurants = new ArrayList<String>();
	private List<String> persons = new ArrayList<String>();

	@Override
	public void setup() {
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			DFService.register(this, dfd);
			PlatformController container = getContainerController();

			String name;
			AgentController ac;

			for (int i = 0; i < numRestaurants; i++) {
				name = "Restaurant_" + (i + 1);
				ac = container.createNewAgent(name, "test.RestaurantAgent", null);
				restaurants.add(name);
				ac.start();
			}

			for (int i = 0; i < 5; i++) {
				// Random Agent
				name = "Person_" + (i + 1);
				ac = container.createNewAgent(name, "test.RandomAgent", null);
				persons.add(name);
				ac.start();

				// Iterative Agent
				name = "Person_" + (numRandom + i + 1);
				ac = container.createNewAgent(name, "test.IterativeAgent", null);
				persons.add(name);
				ac.start();

				// Probability Agent
				name = "Person_" + (numRandom + numIterative + i + 1);
				ac = container.createNewAgent(name, "test.ProbabilityAgent", null);
				persons.add(name);
				ac.start();

				// Reiterative Agent
				// name = "Person_" + (numRandom + numIterative + numProbability + i + 1);
				// ac = container.createNewAgent(name, "test.ReierativeAgent", null);
				// persons.add(name);
				// ac.start();
			}

			// Send message to start polls once every agent has started
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.setContent("START_POLL");

			for (String person : persons)
				message.addReceiver(new AID(person, AID.ISLOCALNAME));

			send(message);

			ParallelBehaviour parallel = new ParallelBehaviour();

			parallel.addSubBehaviour(new TickerBehaviour(this, 10000000) {
				@Override
				public void onTick() {
					ACLMessage message = new ACLMessage(ACLMessage.INFORM);
					message.setContent("NEW_NIGHT");

					for (String restaurant : restaurants)
						message.addReceiver(new AID(restaurant, AID.ISLOCALNAME));

					for (String person : persons)
						message.addReceiver(new AID(person, AID.ISLOCALNAME));

					send(message);
					nightIndex++;

					System.out.println(String.format("\n[Night %d] Has started.", nightIndex));
				}
			});

			addBehaviour(parallel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

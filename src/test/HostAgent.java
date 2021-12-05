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
	public static Integer numRestaurants = 6;
	public static Integer numRandom = 5;
	public static Integer numIterative = 5;
	public static Integer numProbability = 5;
	public static Integer numReiterative = 5;
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

			String name = "";
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
				// name = "Person_" + (numRandom + numIterative + i + 1);
				// ac = container.createNewAgent(name, "test.ProbabilityAgent", null);
				// persons.add(name);
				// ac.start();

				// Reiterative Agent
				// name = "Person_" + (numRandom + numIterative + numProbability + i + 1);
				// ac = container.createNewAgent(name, "test.ReierativeAgent", null);
				// persons.add(name);
				// ac.start();
			}

			ParallelBehaviour parallel = new ParallelBehaviour();

			parallel.addSubBehaviour(new TickerBehaviour(this, 5000) {
				@Override
				public void onTick() {
					ACLMessage message = new ACLMessage(ACLMessage.INFORM);
					message.setContent("NEW_NIGHT");

					for (String name: restaurants)
						message.addReceiver(new AID(name, AID.ISLOCALNAME));

					for (String name: persons)
						message.addReceiver(new AID(name, AID.ISLOCALNAME));

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

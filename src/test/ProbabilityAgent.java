package test;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class ProbabilityAgent extends Agent {
	private Random rand = new Random(System.currentTimeMillis());
	private Integer[] capacities = HostAgent.capacities;
	private Boolean canRequest = false;
	private Integer selected = 0;
	public Integer restaurantIdx = 0;
	public Integer totalPersons = 0;
	public Integer personIdx = 0;
	
	private Boolean startPoll = false;
	private Boolean pollFinished = false;
	private Integer toPoll = 0;
	private Integer polledPeople = 0;

	private Integer[] pollResponses = new Integer[HostAgent.capacities.length];
	private Float[] probabilities = new Float[HostAgent.capacities.length];
	private Integer[] probabilityIndexes = new Integer[HostAgent.capacities.length];
	private Integer probabilityIndex = 0;

	@Override
	public void setup() {
		personIdx = Integer.parseInt(getLocalName().replaceFirst("Person_", ""));
		selected = 1 + rand.nextInt(capacities.length - 1);
		for (int i = 0; i < pollResponses.length; i++) pollResponses[i] = 0;

		try {
			DFAgentDescription description = new DFAgentDescription();
			description.setName(getAID());
			DFService.register(this, description);

			ParallelBehaviour parallel = new ParallelBehaviour();

			parallel.addSubBehaviour(new TickerBehaviour(this, 100) {
				@Override
				protected void onTick() {
					if (startPoll) {
						Integer[] toPollIdx = new Integer[totalPersons - 1];

						for (int i = 0; i < toPollIdx.length; i++) {
							if (i >= personIdx - 1) toPollIdx[i] = i + 1;
							else toPollIdx[i] = i;

							// System.out.println(String.format("[%s] Considering %d.", getLocalName(), toPollIdx[i]+1));
						}
							

						for (int i = 0; i < toPollIdx.length; i++) {
							int j = rand.nextInt(toPollIdx.length - i);
							int temp = toPollIdx[i];
							toPollIdx[i] = toPollIdx[i + j];
							toPollIdx[i + j] = temp;

							// System.out.println(String.format("[%s] Shuffling %d.", getLocalName(), toPollIdx[i] + 1));
						}

						for (int i = 0; i < toPoll; i++) {
							ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
							String personName = "Person_" + (toPollIdx[i] + 1);
							msg.addReceiver(new AID(personName, AID.ISLOCALNAME));
							msg.setContent("POLL");
							send(msg);

							// System.out.println(String.format("[%s] Sending poll to %s.", getLocalName(), personName));
						}

						startPoll = false;
					}

					if (polledPeople == toPoll && !pollFinished) {
						pollFinished = true;

						// String toPrint = String.format("[%s] Poll of %d people finished.", getLocalName(), toPoll);
						for (int i = 0; i < pollResponses.length; i++) {
							probabilities[i] = 1 - ((float) pollResponses[i] / (float) capacities[i]);
							// toPrint += String.format("\nRestaurant_%d: %d - %f", i + 1, pollResponses[i], probabilities[i]);
						}
						// System.out.println(toPrint);

						
						for (int i = 0; i < probabilityIndexes.length; i++)
							probabilityIndexes[i] = i;
						
						for (int i = 0; i < probabilities.length - 1; i++) {
							for (int j = i + 1; j < probabilities.length; j++) {
								if (probabilities[i] < probabilities[j]) {
									float temp = probabilities[i];
									probabilities[i] = probabilities[j];
									probabilities[j] = temp;
				
									int tempo = probabilityIndexes[i];
									probabilityIndexes[i] = probabilityIndexes[j];
									probabilityIndexes[j] = tempo;
								}
							}
						}

						canRequest = true;
					}

					if (canRequest) {
						String restaurantName = "Restaurant_" + (probabilityIndexes[probabilityIndex] + 1);

						ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
						message.setContent("CHECK");
						message.addReceiver(new AID(restaurantName, AID.ISLOCALNAME));
						send(message);
						
						canRequest = false;

						// System.out.println(String.format("[%s] Calling %s for available slots.", getLocalName(), restaurantName));
					}
				}
			});

			parallel.addSubBehaviour(new CyclicBehaviour(this) {
				@Override
				public void action() {
					ACLMessage message = receive();
					if (message != null) {
						ACLMessage reply = message.createReply();
						String content = message.getContent();
						String sender = message.getSender().getLocalName();

						if (content.startsWith("SLOTS")) {
							Integer slots = Integer.parseInt(content.replaceFirst("SLOTS_", ""));
							// System.out.println(String.format("[%s] Received %d free slots from %s.", getLocalName(), slots, sender));

							if (slots > 0) {
								reply.setContent("RESERVE");
								send(reply);

								System.out.println(String.format("[%s] Making a probability reservation at %s.", getLocalName(), sender));
							} else {
								selected = 1 + rand.nextInt(capacities.length - 1);
								canRequest = false;
							}
						}

						if (content.startsWith("SELECTED")) {
							Integer selection = Integer.parseInt(content.replaceFirst("SELECTED_", ""));
							pollResponses[selection - 1]++;
							polledPeople++;

							// System.out.println(String.format("[%s] Poll: %s has selected Restaurant_%d.", getLocalName(), sender, selection));
						}

						if (content.startsWith("ADMITTED")) {
							restaurantIdx = Integer.parseInt(sender.replaceFirst("Restaurant_", ""));
						}

						if (content.startsWith("REJECTED")) {
							probabilityIndex++;
						}

						if (content.startsWith("START_POLL")) {
							totalPersons = HostAgent.totalPersons;
							toPoll = 1 + rand.nextInt(totalPersons - 2);
							startPoll = true;
						}

						if (content.startsWith("POLL")) {
							reply.setContent("SELECTED_" + selected);
							send(reply);
						}

						if (content.startsWith("NEW_NIGHT")) {
							selected = 1 + rand.nextInt(capacities.length - 1);
							restaurantIdx = 0;

							toPoll = 1 + rand.nextInt(totalPersons - 2);
							startPoll = true;
							polledPeople = 0;
							pollFinished = false;
						}
					}
				}
			});

			addBehaviour(parallel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

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
	private Integer[] capacities = HostAgent.capacities;
	private Boolean canRequest = false;
	private Boolean pollSent = false;
	private Boolean pollFinished = false;
	private Random rand = new Random(System.currentTimeMillis());
	private Integer selected = 0;
	private Integer toPoll = 0;
	private Integer polledPeople = 0;
	public Integer restaurantIdx = 0;
	public Integer totalPersons = HostAgent.totalPersons;
	public Integer personIdx = 0;
	public Integer[] pollResponses = { 0, 0, 0, 0, 0, 0 };

	@Override
	public void setup() {
		personIdx = Integer.parseInt(getLocalName().replaceFirst("Person_", ""));
		selected = rand.nextInt(capacities.length) + 1;
		toPoll = 1 + rand.nextInt(totalPersons - 2);

		try {
			DFAgentDescription description = new DFAgentDescription();
			description.setName(getAID());
			DFService.register(this, description);

			ParallelBehaviour parallel = new ParallelBehaviour();

			parallel.addSubBehaviour(new TickerBehaviour(this, 100) {
				@Override
				protected void onTick() {
					if (!pollSent) {
						// Random sort array of 0 to totalPersons excluding .this agent
						Integer[] toPollIdx = new Integer[totalPersons - 1];

						for (int i = 0; i < totalPersons - 2; i++)
							if (i != personIdx)
								toPollIdx[i] = i;

						for (int i = 0; i < totalPersons - 2; i++) {
							int j = rand.nextInt(totalPersons - 2 - i);
							int temp = toPollIdx[i];
							toPollIdx[i] = toPollIdx[i + j];
							toPollIdx[i + j] = temp;
						}

						// First toPoll integers from array are selected
						for (int i = 0; i < toPoll; i++) {
							ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
							String personName = "Person_" + toPollIdx[i];
							msg.addReceiver(new AID(personName, AID.ISLOCALNAME));
							msg.setContent("POLL");
							send(msg);

							// System.out.println(String.format("[%s] Sending poll to %s.", getLocalName(), personName));
						}

						pollSent = true;
					}

					if (polledPeople == toPoll && !pollFinished) {
						pollFinished = true;
						// Make calculations of probability

						System.out.println(String.format("[%s] Polling finished.", getLocalName()));

						for (int i = 0; i < pollResponses.length; i++) {
							System.out.println(String.format("[%s] Poll results: Restaurant_%d - %d.", getLocalName(), i + 1, pollResponses[i]));
						}

						// Flag of calculations have been made

						// canRequest = true;
						// polledPeople = 0;
						// toPoll = 1 + rand.nextInt(totalPersons - 1);
					}

					if (canRequest) {
						// First index of probability

						String restaurantName = "Restaurant_" + selected;

						ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
						message.setContent("CHECK");
						message.addReceiver(new AID(restaurantName, AID.ISLOCALNAME));
						send(message);

						System.out.println(String.format("[%s] Calling %s for available slots.", getLocalName(), restaurantName));
						canRequest = false;
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
							System.out.println(String.format("[%s] Received %d free slots from %s.", getLocalName(), slots, sender));

							if (slots > 0) {
								reply.setContent("RESERVE");
								send(reply);

								System.out.println(String.format("[%s] Making a probability reservation at %s.", getLocalName(), sender));
							} else {
								selected = rand.nextInt(capacities.length);
								canRequest = false;
							}
						}

						if (content.startsWith("SELECTED")) {
							Integer selection = Integer.parseInt(content.replaceFirst("SELECTED_", ""));
							pollResponses[selection - 1]++;
							polledPeople++;
							// System.out.println(String.format("[%s] Poll: %s has selected Restaurant_%d.", getLocalName(), sender, selection));
						}

						switch (content) {
							case "ADMITTED":
								restaurantIdx = Integer.parseInt(sender.replaceFirst("Restaurant_", ""));
								break;
							case "REJECTED":
								// selected = rand.nextInt(capacities.length);
								// canRequest = true;
								break;
							case "POLL":
								reply.setContent("SELECTED_" + selected);
								send(reply);
								break;
							case "NEW_NIGHT":
								restaurantIdx = -1;
								selected = rand.nextInt(capacities.length) + 1;
								break;
							default:
								break;
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

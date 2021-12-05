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

public class IterativeAgent extends Agent {
	private Integer[] capacities = HostAgent.capacities;
	private Boolean messageSent = false;
	private Random rand = new Random(System.currentTimeMillis());
	private Integer selected = 0;
	private Integer restaurantIdx = 0;

	@Override
	public void setup() {
		selected = rand.nextInt(capacities.length) + 1;

		try {
			DFAgentDescription description = new DFAgentDescription();
			description.setName(getAID());
			DFService.register(this, description);

			ParallelBehaviour parallel = new ParallelBehaviour();

			parallel.addSubBehaviour(new TickerBehaviour(this, 100) {
				@Override
				protected void onTick() {
					if (!messageSent) {
						if (restaurantIdx > 0)
							selected = restaurantIdx;

						String restaurantName = "Restaurant_" + selected;

						ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
						message.setContent("CHECK");
						message.addReceiver(new AID(restaurantName, AID.ISLOCALNAME));
						send(message);

						// System.out.println(String.format("[%s] Calling %s for available slots.", getLocalName(), restaurantName));
						messageSent = true;
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

								// System.out.println(String.format("[%s] Making a iterative reservation at %s.", getLocalName(), sender));
							} else {
								selected = rand.nextInt(capacities.length);
								messageSent = false;
							}
						}

						switch (content) {
							case "ADMITTED":
								restaurantIdx = Integer.parseInt(sender.replaceFirst("Restaurant_", ""));
								break;
							case "REJECTED":
								selected = rand.nextInt(capacities.length);
								messageSent = false;
								break;
							case "POLL":
								reply.setContent("SELECTED_" + selected);
								send(reply);
								break;
							case "NEW_NIGHT":
								messageSent = false;
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

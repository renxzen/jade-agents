package finaltest;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAgent extends Agent {
    private Integer capacity;
    private List<String> customers = new ArrayList<String>();

    @Override
    public void setup() {
        capacity =
                HostAgent.capacities[
                        Integer.parseInt(getLocalName().replaceFirst("Restaurant_", "")) - 1];
        System.out.println(String.format("[%s] Ready with %d slots.", getLocalName(), capacity));

        try {
            DFAgentDescription description = new DFAgentDescription();
            description.setName(getAID());
            DFService.register(this, description);

            ParallelBehaviour parallel = new ParallelBehaviour();

            parallel.addSubBehaviour(
                    new CyclicBehaviour(this) {
                        @Override
                        public void action() {
                            ACLMessage message = receive();

                            if (message != null) {
                                ACLMessage reply = message.createReply();
                                String content = message.getContent();
                                String sender = message.getSender().getLocalName();

                                switch (content) {
                                    case "CHECK":
                                        reply.setPerformative(ACLMessage.INFORM);
                                        reply.setContent("SLOTS_" + capacity.toString());
                                        break;
                                    case "RESERVE":
                                        if (customers.size() < capacity) {
                                            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                            reply.setContent("ADMITTED");
                                            customers.add(sender);
                                            System.out.println(
                                                    String.format(
                                                            "[%s] Accepted reservation from %s."
                                                                + " %d/%d.",
                                                            getLocalName(),
                                                            sender,
                                                            customers.size(),
                                                            capacity));
                                        } else {
                                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                            reply.setContent("REJECTED");
                                            System.out.println(
                                                    String.format(
                                                            "[%s] Rejected reservation from %s."
                                                                + " %d/%d.",
                                                            getLocalName(),
                                                            sender,
                                                            customers.size(),
                                                            capacity));
                                        }
                                        break;
                                    case "NEW_NIGHT":
                                        System.out.println(
                                                String.format(
                                                        "[%s] Ready with %d slots.",
                                                        getLocalName(), capacity));
                                        customers.clear();
                                        break;
                                    default:
                                        break;
                                }

                                send(reply);
                            }
                        }
                    });

            addBehaviour(parallel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

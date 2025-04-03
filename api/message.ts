const connect = () =>  {} // USER /message/:user-id/:listing-id

// WEBSOCKET CLIENT ENDPOINTS
const send = ({
  content: string
}) => {}

// WEBSOCKET SERVER ENDPOINTS
const broadcast = (message: Message) => { };

const history = ({page, pagesize}) => {} // USER GET /message/:user-id/:listing-id

interface Message {
  id: string; // uuid
  sender: string; // user-id
  content: string;
  time: Date;
}































import { Client } from '@stomp/stompjs'

let stompClient = null

export const connectSeatSocket = (scheduleId, onMessageReceived) => {

  stompClient = new Client({
    brokerURL: 'ws://localhost:8080/ws',
    reconnectDelay: 5000,

    onConnect: () => {
      console.log('Connected')

      stompClient.subscribe(
        `/topic/seats/${scheduleId}`,
        (message) => {
          const data = JSON.parse(message.body)
          onMessageReceived(data)
        }
      )
    },

    onStompError: (frame) => {
      console.error(frame)
    },
  })

  stompClient.activate()
}

export const disconnectSeatSocket = () => {
  if (stompClient) {
    stompClient.deactivate()
  }
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MessageDTO } from '../models/dtos/message.dto';
import { environment } from 'src/environments/environment.prod';
import { Message } from '../models/message.model';

const SERVER_URL = environment.serverUrl;
const MESSAGES_API = environment.messagesApiRoute;

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private http: HttpClient) { }

  addMessage(message: Message) {
    return this.http.post<string>(SERVER_URL + MESSAGES_API, Message.toDTO(message));
  }

  updateMessage(message: Message) {
    return this.http.put<string>(SERVER_URL + MESSAGES_API, Message.toDTO(message));
  }

  getPresentationMessages(presentationId: string) {
    return this.http.get<MessageDTO[]>(SERVER_URL + MESSAGES_API, {
      params: {
        presentationId: presentationId
      }
    });
  }

  deleteMessage(id: string) {
    return this.http.delete<string>(SERVER_URL + MESSAGES_API + `/${id}`);
  }
}

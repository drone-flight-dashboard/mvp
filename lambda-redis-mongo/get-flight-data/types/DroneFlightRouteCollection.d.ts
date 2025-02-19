import { ObjectId } from 'mongodb';

export interface DroneFlightCollection {
    _id: ObjectId;
    key: string;
    value: string;
}

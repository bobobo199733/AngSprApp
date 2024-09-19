export class Patient{
    id:string | null = null;
    name: string;
    dateOfBirth: string;
    gender: string;
    phoneNumber: string;
    constructor(name: string, gender: string, dateOfBirth: string, phoneNumber: string) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
    }
    
}
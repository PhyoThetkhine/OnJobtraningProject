export interface Address {
  additionalAddress: string;
  city: string;
  state: string;
  township: string;
}

// Remove UserCIFBase since we're defining our own properties
export interface CifDTO {
  gender: string;
  dateOfBirth: string;
  NRCBackPhoto: string;
  NRCFrontPhoto: string;
  createdUser: string;
  nrc: any;
  id: number;
  cifCode: string;
  name: string;
  email: string;
  phoneNumber: string;
  NRC: string;
  userCode: string; // userCode field
  branchName: string;
  state : string;
  township : string;
  city : string;
  additionalAddress : string;
  // branchName field
  };

  



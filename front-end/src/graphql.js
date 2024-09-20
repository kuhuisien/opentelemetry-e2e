import { gql, useQuery } from "@apollo/client";

const GET_USERS = gql`
  query bookDetails {
    bookById(id: "book-1") {
      id
      name
      pageCount
      author {
        id
        firstName
        lastName
      }
    }
  }
`;

export const getBook = () => {
  return useQuery(GET_USERS);
};

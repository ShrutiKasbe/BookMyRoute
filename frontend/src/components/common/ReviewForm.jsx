import React, { useState } from "react";
import axios from "axios";

const ReviewForm = () => {

  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState("");

  const submitReview = async () => {

    const reviewData = {
      rating: rating,
      comment: comment,
      routeId: 1,
      userName: "Shruti"
    };

    try {

      const token = localStorage.getItem("token");

await axios.post(
  "http://localhost:8080/api/reviews",
  reviewData,
  {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
);

      alert("Review Added Successfully");

      setRating("");
      setComment("");

    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div>

      <h2>Give Review</h2>

      <div>
  <h3>Give Rating</h3>

  {[1, 2, 3, 4, 5].map((star) => (
    <span
      key={star}
      onClick={() => setRating(star)}
      style={{
        fontSize: "30px",
        cursor: "pointer",
        color: rating >= star ? "gold" : "gray"
      }}
    >
      ★
    </span>
  ))}
</div>

      <br /><br />

      <textarea
        placeholder="Write comment"
        value={comment}
        onChange={(e) => setComment(e.target.value)}
      />

      <br /><br />

      <button onClick={submitReview}>
        Submit Review
      </button>

    </div>
  );
};

export default ReviewForm;
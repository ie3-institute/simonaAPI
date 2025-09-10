/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import edu.ie3.datamodel.models.result.system.FlexOptionsResult;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.quantity.Power;
import org.slf4j.Logger;
import tech.units.indriya.ComparableQuantity;

/**
 * Extended {@link FlexOptionsResult}, that contains the receiver of the flex options. This models
 * may also contain a disaggregation of the total flex options.
 */
public final class ExtendedFlexOptionsResult extends FlexOptionsResult implements EmData {

  /** The receiver of the message. */
  private final UUID receiver;

  /** The disaggregated flex option results. */
  private final Map<UUID, FlexOptionsResult> disaggregated;

  /**
   * Standard constructor for {@link ExtendedFlexOptionsResult}.
   *
   * @param time date and time when the result is produced
   * @param sender uuid of the input model that produces the result
   * @param receiver uuid of the receiver that will receive this result
   * @param pRef active power that was suggested for regular usage by the system participant
   * @param pMin active minimal power that was determined by the system participant
   * @param pMax active maximum power that was determined by the system participant
   */
  public ExtendedFlexOptionsResult(
      ZonedDateTime time,
      UUID sender,
      UUID receiver,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax) {
    super(time, sender, pRef, pMin, pMax);
    this.receiver = receiver;
    this.disaggregated = new HashMap<>();
  }

  /**
   * Constructor for {@link ExtendedFlexOptionsResult} with disaggregated flex options.
   *
   * @param time date and time when the result is produced
   * @param sender uuid of the input model that produces the result
   * @param receiver uuid of the receiver that will receive this result
   * @param pRef active power that was suggested for regular usage by the system participant
   * @param pMin active minimal power that was determined by the system participant
   * @param pMax active maximum power that was determined by the system participant
   */
  public ExtendedFlexOptionsResult(
      ZonedDateTime time,
      UUID sender,
      UUID receiver,
      ComparableQuantity<Power> pRef,
      ComparableQuantity<Power> pMin,
      ComparableQuantity<Power> pMax,
      Map<UUID, FlexOptionsResult> disaggregated) {
    super(time, sender, pRef, pMin, pMax);
    this.receiver = receiver;
    this.disaggregated = disaggregated;
  }

  /**
   * Method for adding disaggregated flex option results to this object.
   *
   * <p>Note: This method does not check, if the disaggregated flex options match the total flex
   * options. To do this, please use the method {@link #checkFlexOptions(Logger)}.
   *
   * @param uuid of the inferior model
   * @param flexOptionsResult the flex options of the inferior model
   */
  public void addDisaggregated(UUID uuid, FlexOptionsResult flexOptionsResult) {
    this.disaggregated.put(uuid, flexOptionsResult);
  }

  /** Returns the uuid of the sender ({@link #getInputModel()}). */
  @Override
  public UUID getSender() {
    return getInputModel();
  }

  @Override
  public UUID getReceiver() {
    return receiver;
  }

  /** Returns {@code true}, if disaggregated flex option are available. */
  public boolean hasDisaggregated() {
    return !disaggregated.isEmpty();
  }

  /**
   * Returns a map: uuid to disaggregated flex options.
   *
   * <p>Note: If no disaggregated flex options are present (see: {@link #hasDisaggregated()}), the
   * map will be empty.
   */
  public Map<UUID, FlexOptionsResult> getDisaggregated() {
    return Collections.unmodifiableMap(disaggregated);
  }

  /**
   * Method for checking if the disaggregated flex options match the total flex options.
   *
   * @param log used for logging
   * @return {@code true} if the flex options match, else {@code false}
   */
  public boolean checkFlexOptions(Logger log) {
    List<ComparableQuantity<Power>> refs = new ArrayList<>();
    List<ComparableQuantity<Power>> mins = new ArrayList<>();
    List<ComparableQuantity<Power>> maxs = new ArrayList<>();

    disaggregated.forEach(
        (uuid, flexOptionsResult) -> {
          refs.add(flexOptionsResult.getpRef());
          mins.add(flexOptionsResult.getpMin());
          maxs.add(flexOptionsResult.getpMax());
        });

    ComparableQuantity<Power> ref = getpRef();
    ComparableQuantity<Power> min = getpMin();
    ComparableQuantity<Power> max = getpMax();

    Optional<ComparableQuantity<Power>> refSum = refs.stream().reduce(ComparableQuantity::add);
    Optional<ComparableQuantity<Power>> minSum = mins.stream().reduce(ComparableQuantity::add);
    Optional<ComparableQuantity<Power>> maxSum = maxs.stream().reduce(ComparableQuantity::add);

    boolean isRefValid = false;
    boolean isMinValid = false;
    boolean isMaxValid = false;

    if (refSum.isPresent()) {
      isRefValid = refSum.get().isEquivalentTo(ref);

      if (!isRefValid) {
        log.warn("Disaggregated reference power does not match total reference power.");
      }
    } else {
      log.warn("Cannot check disaggregated reference power.");
    }

    if (minSum.isPresent()) {
      isMinValid = minSum.get().isEquivalentTo(min);

      if (!isMinValid) {
        log.warn("Disaggregated minimum power does not match total minimum power.");
      }
    } else {
      log.warn("Cannot check disaggregated minimum power.");
    }

    if (maxSum.isPresent()) {
      isMaxValid = maxSum.get().isEquivalentTo(max);

      if (!isMaxValid) {
        log.warn("Disaggregated maximum power does not match total maximum power.");
      }
    } else {
      log.warn("Cannot check disaggregated maximum power.");
    }

    return isRefValid && isMinValid && isMaxValid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExtendedFlexOptionsResult that = (ExtendedFlexOptionsResult) o;
    return Objects.equals(receiver, that.receiver)
        && Objects.equals(disaggregated, that.disaggregated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), receiver, disaggregated);
  }

  @Override
  public String toString() {
    return "ExtendedFlexOptionsResult{"
        + "time="
        + getTime()
        + ", sender="
        + getSender()
        + ", receiver="
        + receiver
        + ", pRef="
        + getpRef()
        + ", pMin="
        + getpMin()
        + ", pMax="
        + getpMax()
        + ", disaggregated="
        + disaggregated
        + '}';
  }
}

package models.services

import models.{ Gathering, Goal }
import scala.concurrent._

/**
 * Service for handling Gathering objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0Gathering
 */
trait GatheringService {
  /**
   * Saves given gathering returning updated gathering if not previously saved.
   *
   * @param gathering Gathering to save
   * @return Updated accumulation if not previously saved, otherwise the unchanged accumulation.
   */
  def save(gathering: Gathering): Future[Gathering]

  /**
   * Finds all Gatherings.
   *
   * @return Collection of found Gatherings.
   */
  def find(): Future[Seq[Gathering]]

  /**
   * Archives gathering specified by given gathering id removing from all find results.
   *
   * @param gatheringId Id of gathering to archive.
   * @return true if found and archived, false if not found
   */
  def delete(gatheringId: Long): Future[Boolean]

  /**
   * Finds Gathering by it's id.
   *
   * @param id Unique id of mantra to find gatherings for
   * @return Found gatherings, if not found future will fail.
   */
  def findByMantra(id: Long): Future[Seq[Gathering]]

  /**
   * Finds a goal by it's gathering and mantra id.
   *
   * @param gatheringId Id of gathering goal belongs to
   * @param mantraId Id of mantra goal refers to
   * @return Goal
   */
  def findGoal(gatheringId: Long, mantraId: Long): Future[Goal]

  /**
   * Add an accumulation goal to a gathering.
   *
   * @param goal Goal to add
   * @return true if successfully added
   */
  def add(goal: Goal): Future[Boolean]

  /**
   * Remove an accumulation goal from a gathering.
   *
   * @param gatheringId Gathering id of Goal to remove
   * @param mantraId Mantra id of Goal to remove
   * @return true if successfully added
   */
  def remove(gatheringId: Long, mantraId: Long): Future[Boolean]
}